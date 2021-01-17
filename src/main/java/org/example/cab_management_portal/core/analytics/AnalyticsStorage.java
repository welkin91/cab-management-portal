package org.example.cab_management_portal.core.analytics;

import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;

public interface AnalyticsStorage {

    public void init();

    public boolean updateCabIdleStatus(Cab cab, CabState state);

    public Long getCabIdleTimeInMillis(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException;
}
