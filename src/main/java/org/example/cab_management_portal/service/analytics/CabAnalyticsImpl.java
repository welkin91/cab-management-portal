package org.example.cab_management_portal.service.analytics;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dto.CabIdleTimeRequest;
import org.example.cab_management_portal.models.dto.CabIdleTimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}