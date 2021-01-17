package org.example.cab_management_portal.service.analytics;

import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dto.CabIdleTimeRequest;
import org.example.cab_management_portal.models.dto.CabIdleTimeResponse;

public interface CabAnalytics {

    public CabIdleTimeResponse getIdleTimeForCab(CabIdleTimeRequest idleTimeRequest) throws AnalyticsException;
}
