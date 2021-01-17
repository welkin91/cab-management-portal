package org.example.cab_management_portal.core.analytics;

import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;

import java.util.List;

public interface AnalyticsStorage {

    public void init();

    public boolean updateCabStatus(Cab cab);

    public Long getCabIdleTimeInMillis(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException;

    public List<CabEntry> getCabStates(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException;
}
