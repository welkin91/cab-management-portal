package org.example.cab_management_portal.core.storage.helpers;

import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public interface TripHelper {

    public Cab bookIdleCab(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String> _allowedCities,
            String city
    ) throws TripException;
}
