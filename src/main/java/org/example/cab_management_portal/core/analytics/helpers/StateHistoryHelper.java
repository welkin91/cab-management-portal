package org.example.cab_management_portal.core.analytics.helpers;

import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

public interface StateHistoryHelper {

    public boolean updateCabStatus(
            Map<String, ConcurrentNavigableMap<Long, CabEntry>> _allStates,
            Cab cab
    );

    public Long getCabIdleTimeInMillis(
            Map<String, ConcurrentNavigableMap<Long, CabEntry>> _allStates,
            String registrationNumber,
            Long startTime,
            Long endTime
    ) throws AnalyticsException;

    public List<CabEntry> getCabStates(
            Map<String, ConcurrentNavigableMap<Long, CabEntry>> _allStates,
            String registrationNumber,
            Long startTime,
            Long endTime
    ) throws AnalyticsException;
}
