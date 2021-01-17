package org.example.cab_management_portal.core.storage.helpers;

import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.LocationUpdate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public interface LocationHelper {

    public boolean updateLocation(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String>_allowedCities,
            LocationUpdate locationUpdate
    ) throws LocationException;
}
