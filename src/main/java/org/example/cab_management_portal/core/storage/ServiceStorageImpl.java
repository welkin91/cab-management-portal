package org.example.cab_management_portal.core.storage;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.core.state_machine.core.impl.CabStateMachine;
import org.example.cab_management_portal.core.storage.helpers.LocationHelper;
import org.example.cab_management_portal.core.storage.helpers.RegistrationHelper;
import org.example.cab_management_portal.core.storage.helpers.StateHelper;
import org.example.cab_management_portal.core.storage.helpers.TripHelper;
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
    RegistrationHelper registrationHelper;

    @Autowired
    LocationHelper locationHelper;

    @Autowired
    StateHelper stateHelper;

    @Autowired
    TripHelper tripHelper;

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
        return registrationHelper.registerCab(cab, _cityLevelIdleCabs, _allCabs, _allowedCities);
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
        return registrationHelper.registerCity(city, _allowedCities);
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
        return locationHelper.updateLocation(_cityLevelIdleCabs, _allCabs, _allowedCities, location);
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
        return stateHelper.updateCabState(_cityLevelIdleCabs, _allCabs, statusUpdate);
    }

    /**
     *
     * @param city
     * @return  Cab object in case, a cab with max IDLE time exists.
     * @throws TripException
     */
    @Override
    public Cab bookIdleCab(String city) throws TripException {
        return tripHelper.bookIdleCab(_cityLevelIdleCabs, _allCabs, _allowedCities, city);
    }
}
