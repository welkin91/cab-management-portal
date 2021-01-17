package org.example.cab_management_portal.core.storage.helpers;

import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.StatusUpdate;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public interface StateHelper {

    public boolean updateCabState(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            StatusUpdate statusUpdate
    ) throws StateException;
}
