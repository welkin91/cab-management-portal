package org.example.cab_management_portal.core.storage.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.core.state_machine.core.impl.CabStateMachine;
import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class TripHelperImpl implements TripHelper {

    @Autowired
    CabStateMachine stateMachine;

    @Autowired
    AnalyticsStorage analyticsStorage;

    @Override
    public Cab bookIdleCab(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String> _allowedCities,
            String city
    ) throws TripException {
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
            insertAllowedCity(_cityLevelIdleCabs, city);
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

    public void insertAllowedCity(Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs, String city) {
        _cityLevelIdleCabs.put(city, new PriorityBlockingQueue<Cab>(
                        2^31 - 1,
                        (a, b) -> (int) (a.getLastUpdatedAt()/1000 - b.getLastUpdatedAt()/1000)
                )
        );
    }
}
