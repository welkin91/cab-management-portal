package org.example.cab_management_portal.controllers.analytics;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.models.dto.CabIdleTimeRequest;
import org.example.cab_management_portal.models.dto.CabIdleTimeResponse;
import org.example.cab_management_portal.models.response.AppResponse;
import org.example.cab_management_portal.service.analytics.CabAnalytics;
import org.example.cab_management_portal.utils.ClassTransformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController()
@Api(value = "cab analytics Base URL's", description = "Contains Api end point to analytics on a cab")
@RequestMapping(value = "/v1/analytics/")
@Slf4j
public class AnalyticsController {

    @Autowired
    CabAnalytics analytics;

    @ApiOperation(
            value = "Get total idle time a Cab",
            notes = "This is used in order to total idle time of a Cab"
    )
    @GetMapping(
            value = "/getIdleTime",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse getIdleCabTime(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws AnalyticsException {

        CabIdleTimeRequest idleTimeRequest = null;
        try {
            idleTimeRequest = ClassTransformationUtil.fromString(requestBody, CabIdleTimeRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        CabIdleTimeResponse response = null;
        try {
            response = analytics.getIdleTimeForCab(idleTimeRequest);
        }
        catch (AnalyticsException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(response == null) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("Analytics for cabId: '" + idleTimeRequest.getRegistrationId() + "' not present.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("Data aggregated successfully.")
                .data(response)
                .build();
    }

    @ApiOperation(
            value = "Get total idle time a Cab",
            notes = "This is used in order to total idle time of a Cab"
    )
    @GetMapping(
            value = "/getCabStates",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse getCabStates(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws AnalyticsException {

        CabIdleTimeRequest idleTimeRequest = null;
        try {
            idleTimeRequest = ClassTransformationUtil.fromString(requestBody, CabIdleTimeRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        List<CabEntry> response = null;
        try {
            response = analytics.getAllStatesForCab(idleTimeRequest);
        }
        catch (AnalyticsException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(response == null || response.size() == 0) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("Analytics for cabId: '" + idleTimeRequest.getRegistrationId() + "' not present.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("Data aggregated successfully.")
                .data(response)
                .build();
    }
}
