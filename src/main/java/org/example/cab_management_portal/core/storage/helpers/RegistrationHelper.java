package org.example.cab_management_portal.core.storage.helpers;

import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.dao.Cab;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public interface RegistrationHelper {

    public boolean registerCab(
            Cab cab, Map<String,
            PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            Set<String>_allowedCities
    ) throws RegistrationException;

    public boolean registerCity(String city, Set<String> _allowedCities) throws RegistrationException;
}
