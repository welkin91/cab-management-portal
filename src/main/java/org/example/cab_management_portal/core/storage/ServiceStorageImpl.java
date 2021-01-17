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

    /**
     *
     * @param cab
     * @return  True if a cab is registered.
     *          False if cab is already present.
     * @throws RegistrationException
     */
    @Override
    public boolean registerCab(Cab cab) throws RegistrationException {
        /**
         * check if city is onboarded.
         */
        if(!_allowedCities.contains(cab.getCity())) {
            throw new RegistrationException("City: '"+cab.getCity()+"' not onboarded yet. Please onboard the city first.");
        }

        /**
         * A cab with IDLE state can only be onboarded.
         */
        if(cab.getState() != CabState.IDLE) {
            throw new RegistrationException("Cab state should be 'IDLE' in order to register.");
        }

        /**
         * Check if the cab already exists or not.
         * Return false if cab already exists.
         */
        if(_allCabs.containsKey(cab.getRegistrationNumber())) {
            return false;
        }

        /**
         * Check if city is initialised or not.
         * In case the city is not initialised, initialise it,
         */
        if(!_cityLevelIdleCabs.containsKey(cab.getCity())) {
            insertAllowedCity(cab.getCity());
        }

        /**
         * Maintain city level idle cabs and all cabs.
         */
        _cityLevelIdleCabs.get(cab.getCity()).offer(cab);
        _allCabs.put(cab.getRegistrationNumber(), cab);

        /**
         * Push status update for this cab to Analytics handler.
         */
        analyticsStorage.updateCabStatus(cab);

        return true;
    }

    /**
     *
     * @param city
     * @return  True if city id added successfully.
     *          False in case, city is already present.
     * @throws RegistrationException
     */
    @Override
    public boolean registerCity(String city) throws RegistrationException {
        /**
         * Check if city is present.
         * Return false if the city is already present.
         */
        if(_allowedCities.contains(city)) {
            return false;
        }

        /**
         * Add it in the allowed cities set.
         */
        _allowedCities.add(city);

        return true;
    }

    /**
     *
     * @param location
     * @return  Return True if the location of the cab is updated successfully.
     *          False, if the location was same as its last location.
     * @throws LocationException
     */
    @Override
    public boolean updateLocation(LocationUpdate location) throws LocationException {
        /**
         * Check if cab exists. If not, throw exception.
         */
        if(!_allCabs.containsKey(location.getRegistrationNumber())) {
            throw new LocationException("Cab with registration number: '" + location.getRegistrationNumber() + "' does not exist.");
        }

        /**
         * Check if new location is allowed or not. If not, throw exception.
         */
        if(!_allowedCities.contains(location.getLocationCity())) {
            throw new LocationException(
                    "Driver can not change its location, because the city: '" +
                    location.getLocationCity() +
                    "' is not onboarded. Please onboard this city before updating the location."
            );
        }

        Cab cab = _allCabs.get(location.getRegistrationNumber());
        String prevLocation = cab.getCity();

        /**
         * Check if last location is same or not.
         */
        if(prevLocation.equals(location.getLocationCity())) {
            throw new LocationException("Location is same as last update. No updates were made.");
        }

        /**
         * If cab state is Idle, remove from previous city bucket.
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(prevLocation).remove(cab);
        }

        cab.setCity(location.getLocationCity());
        cab.setLastUpdatedAt(System.currentTimeMillis());

        _allCabs.put(location.getRegistrationNumber(), cab);

        /**
         * Initialise city in case new location is not present.
         */
        if(!_cityLevelIdleCabs.containsKey(location.getLocationCity())) {
            insertAllowedCity(location.getLocationCity());
        }

        /**
         * If cab state is Idle, add cab to new city bucket.
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location.getLocationCity()).offer(cab);
        }

        return true;
    }

    /**
     *
     * @param statusUpdate
     * @return  True if the state of a cab was updated.
     *          False, in case the state was same as previous state.
     * @throws StateException
     */
    @Override
    public boolean updateCabState(StatusUpdate statusUpdate) throws StateException {
        /**
         * Check if cab exists. else, throw an exception.
         */
        if(!_allCabs.containsKey(statusUpdate.getRegistrationNumber())) {
            throw new StateException("Cab with registration number: '" + statusUpdate.getRegistrationNumber() + "' does not exist.");
        }

        Cab cab = _allCabs.get(statusUpdate.getRegistrationNumber());
        String location = cab.getCity();

        /**
         * return false if the state is same as previous.
         */
        if(cab.getState() == statusUpdate.getState()) {
            return false;
        }

        /**
         * validate if the jump from previous state and current input state is present or not.
         */
        boolean hopAllowed = stateMachine.validate(cab.getState(), statusUpdate.getState());

        /**
         * If not, throw and exception.
         */
        if(!hopAllowed) {
            throw new StateException("State change from '" + cab.getState() + "' to '" + statusUpdate.getState() + "' is not allowed.");
        }

        /**
         * if previous state was IDLE, remove it from the IDLE cabs
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location).remove(cab);
        }

        cab.setState(statusUpdate.getState());
        cab.setLastUpdatedAt(System.currentTimeMillis());

        _allCabs.put(statusUpdate.getRegistrationNumber(), cab);

        /**
         * Pass current updated state of a cab to analytics.
         */
        analyticsStorage.updateCabStatus(cab);

        /**
         * if new state is IDLE, add it back to the IDLE cabs set.
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(cab.getCity()).offer(cab);
        }

        return true;
    }

    /**
     *
     * @param city
     * @return  Cab object in case, a cab with max IDLE time exists.
     * @throws TripException
     */
    @Override
    public Cab bookIdleCab(String city) throws TripException {
        Cab bookedCab = null;

        /**
         * Check if city is allowed for booking.
         */
        if(!_allowedCities.contains(city)) {
            throw new TripException("Cab booking not available in city: " + city);
        }

        /**
         * Check if cabs exists in the city.
         */
        if(!_cityLevelIdleCabs.containsKey(city)) {
            insertAllowedCity(city);
        }

        /**
         * Check if IDLE cabs are present.
         */
        if(_cityLevelIdleCabs.get(city).size() == 0) {
            analyticsStorage.addDemand(city);
            throw new TripException("No cabs available in city: " + city + ". Please try again later.");
        }

        bookedCab = _cityLevelIdleCabs.get(city).poll();
        bookedCab.setState(
                stateMachine.getNextState(bookedCab.getState())
        );

        _allCabs.put(bookedCab.getRegistrationNumber(), bookedCab);

        /**
         * Update current state to analytics.
         */
        analyticsStorage.updateCabStatus(bookedCab);
        analyticsStorage.removeDemand(city);

        return bookedCab;
    }

    /**
     * Initilise city level Priority Queue.
     * Priority will be current time in seconds.
     * Lesser the time, more should be the priority.
     * @param city
     */
    public void insertAllowedCity(String city) {
        _cityLevelIdleCabs.put(city, new PriorityBlockingQueue<Cab>(
                2^31 - 1,
                (a, b) -> (int) (a.getLastUpdatedAt()/1000 - b.getLastUpdatedAt()/1000)
            )
        );
    }
}
