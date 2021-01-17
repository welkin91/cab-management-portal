package org.example.cab_management_portal.service.analytics;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.models.dao.DemandEntry;
import org.example.cab_management_portal.models.dto.CabIdleTimeRequest;
import org.example.cab_management_portal.models.dto.CabIdleTimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CabAnalyticsImpl implements CabAnalytics {

    @Autowired
    AnalyticsStorage analyticsStorage;

    @Override
    public CabIdleTimeResponse getIdleTimeForCab(CabIdleTimeRequest idleTimeRequest) throws AnalyticsException {
        if(idleTimeRequest == null) {
            throw new AnalyticsException("Idle time request object is empty.");
        }

        Long idleTime = analyticsStorage.getCabIdleTimeInMillis(
                idleTimeRequest.getRegistrationId(),
                idleTimeRequest.getStartTime(),
                idleTimeRequest.getEndTime()
        );

        return CabIdleTimeResponse.builder()
                .registrationId(idleTimeRequest.getRegistrationId())
                .startTime(idleTimeRequest.getStartTime())
                .endTime(idleTimeRequest.getEndTime())
                .totalIdleTime(idleTime)
                .build();
    }

    @Override
    public List<CabEntry> getAllStatesForCab(CabIdleTimeRequest idleTimeRequest) throws AnalyticsException {
        if(idleTimeRequest == null) {
            throw new AnalyticsException("Idle time request object is empty.");
        }

        return analyticsStorage.getCabStates(
                idleTimeRequest.getRegistrationId(),
                idleTimeRequest.getStartTime(),
                idleTimeRequest.getEndTime()
        );
    }

    @Override
    public List<DemandEntry> getDemandedCities() throws AnalyticsException {
        return analyticsStorage.getHighDemandCityData();
    }
}
