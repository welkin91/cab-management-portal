package org.example.cab_management_portal.core.storage.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class RegistrationHelperImpl implements RegistrationHelper {

    @Autowired
    AnalyticsStorage analyticsStorage;

    @Override
    public boolean registerCab(
            Cab cab,
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String> _allowedCities
    ) throws RegistrationException {
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
            insertAllowedCity(_cityLevelIdleCabs, cab.getCity());
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

    @Override
    public boolean registerCity(String city, Set<String> _allowedCities) throws RegistrationException {
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

    public void insertAllowedCity(Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs, String city) {
        _cityLevelIdleCabs.put(city, new PriorityBlockingQueue<Cab>(
                        2^31 - 1,
                        (a, b) -> (int) (a.getLastUpdatedAt()/1000 - b.getLastUpdatedAt()/1000)
                )
        );
    }
}
