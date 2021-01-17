package org.example.cab_management_portal.core.storage.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.LocationUpdate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class LocationHelperImpl implements LocationHelper {


    @Override
    public boolean updateLocation(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String> _allowedCities,
            LocationUpdate location
    ) throws LocationException {
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
            insertAllowedCity(_cityLevelIdleCabs, location.getLocationCity());
        }

        /**
         * If cab state is Idle, add cab to new city bucket.
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location.getLocationCity()).offer(cab);
        }

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
