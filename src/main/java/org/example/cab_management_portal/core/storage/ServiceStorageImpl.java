package org.example.cab_management_portal.core.storage;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.core.state_machine.core.impl.CabStateMachine;
import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.LocationUpdate;
import org.example.cab_management_portal.models.dao.StatusUpdate;
import org.example.cab_management_portal.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class ServiceStorageImpl implements ServiceStorage {

    Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs;
    Map<String, Cab> _allCabs;
    Set<String> _allowedCities;

    @Autowired
    CityHandler cityHandler;

    @Autowired
    CabStateMachine stateMachine;

    @Autowired
    AnalyticsStorage analyticsStorage;

    @Override
    public void init() {
        _allCabs = new ConcurrentHashMap<>();
        _allowedCities = cityHandler.getBaseAllowedCities();
        _cityLevelIdleCabs = new ConcurrentHashMap<>();
    }

    @Override
    public boolean registerCab(Cab cab) throws RegistrationException {
        if(!_allowedCities.contains(cab.getCity())) {
            throw new RegistrationException("City: '"+cab.getCity()+"' not onboarded yet. Please onboard the city first.");
        }

        if(cab.getState() != CabState.IDLE) {
            throw new RegistrationException("Cab state should be 'IDLE' in order to register.");
        }

        if(_allCabs.containsKey(cab.getRegistrationNumber())) {
            return false;
        }

        if(!_cityLevelIdleCabs.containsKey(cab.getCity())) {
            insertAllowedCity(cab.getCity());
        }

        _cityLevelIdleCabs.get(cab.getCity()).offer(cab);
        _allCabs.put(cab.getRegistrationNumber(), cab);

        analyticsStorage.updateCabIdleStatus(cab, CabState.IDLE_START);
        analyticsStorage.updateCabStatus(cab);

        return true;
    }

    @Override
    public boolean registerCity(String city) throws RegistrationException {
        if(_allowedCities.contains(city)) {
            return false;
        }

        _allowedCities.add(city);

        return true;
    }

    @Override
    public boolean updateLocation(LocationUpdate location) throws LocationException {
        if(!_allCabs.containsKey(location.getRegistrationNumber())) {
            throw new LocationException("Cab with registration number: '" + location.getRegistrationNumber() + "' does not exist.");
        }

        if(!_allowedCities.contains(location.getLocationCity())) {
            throw new LocationException(
                    "Driver can not change its location, because the city: '" +
                    location.getLocationCity() +
                    "' is not onboarded. Please onboard this city before updating the location."
            );
        }

        Cab cab = _allCabs.get(location.getRegistrationNumber());
        String prevLocation = cab.getCity();

        if(prevLocation.equals(location.getLocationCity())) {
            throw new LocationException("Location is same as last update. No updates were made.");
        }

        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(prevLocation).remove(cab);
        }

        cab.setCity(location.getLocationCity());
        cab.setLastUpdatedAt(System.currentTimeMillis());

        _allCabs.put(location.getRegistrationNumber(), cab);

        if(!_cityLevelIdleCabs.containsKey(location.getLocationCity())) {
            insertAllowedCity(location.getLocationCity());
        }

        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location.getLocationCity()).offer(cab);
        }

        return true;
    }

    @Override
    public boolean updateCabState(StatusUpdate statusUpdate) throws StateException {
        if(!_allCabs.containsKey(statusUpdate.getRegistrationNumber())) {
            throw new StateException("Cab with registration number: '" + statusUpdate.getRegistrationNumber() + "' does not exist.");
        }

        Cab cab = _allCabs.get(statusUpdate.getRegistrationNumber());
        String location = cab.getCity();

        if(cab.getState() == statusUpdate.getState()) {
            return false;
        }

        boolean hopAllowed = stateMachine.validate(cab.getState(), statusUpdate.getState());
        if(!hopAllowed) {
            throw new StateException("State change from '" + cab.getState() + "' to '" + statusUpdate.getState() + "' is not allowed.");
        }

        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location).remove(cab);
        }

        cab.setState(statusUpdate.getState());
        cab.setLastUpdatedAt(System.currentTimeMillis());

        _allCabs.put(statusUpdate.getRegistrationNumber(), cab);
        analyticsStorage.updateCabStatus(cab);

        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(cab.getCity()).offer(cab);
            analyticsStorage.updateCabIdleStatus(cab, CabState.IDLE_START);
        }

        if(cab.getState().equals(CabState.TRIP_ASSIGNED))  {
            analyticsStorage.updateCabIdleStatus(cab, CabState.IDLE_END);
        }

        return true;
    }

    @Override
    public Cab bookIdleCab(String city) throws TripException {
        Cab bookedCab = null;

        if(!_allowedCities.contains(city)) {
            throw new TripException("Cab booking not available in city: " + city);
        }

        if(!_cityLevelIdleCabs.containsKey(city)) {
            insertAllowedCity(city);
        }

        if(_cityLevelIdleCabs.get(city).size() == 0) {
            throw new TripException("No cabs available in city: " + city + ". Please try again later.");
        }

        bookedCab = _cityLevelIdleCabs.get(city).poll();
        bookedCab.setState(
                stateMachine.getNextState(bookedCab.getState())
        );

        _allCabs.put(bookedCab.getRegistrationNumber(), bookedCab);
        analyticsStorage.updateCabStatus(bookedCab);
        analyticsStorage.updateCabIdleStatus(bookedCab, CabState.IDLE_END);

        return bookedCab;
    }

    public void insertAllowedCity(String city) {
        _cityLevelIdleCabs.put(city, new PriorityBlockingQueue<Cab>(
                2^31 - 1,
                (a, b) -> (int) (a.getLastUpdatedAt()/1000 - b.getLastUpdatedAt()/1000)
            )
        );
    }
}
