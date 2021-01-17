package org.example.cab_management_portal.controllers.state;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.models.dto.UpdateStateRequest;
import org.example.cab_management_portal.models.response.AppResponse;
import org.example.cab_management_portal.service.state.StateUpdation;
import org.example.cab_management_portal.utils.ClassTransformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
@Api(value = "cab state Base URL's", description = "Contains Api end point to update state of a cab")
@RequestMapping(value = "/v1/state/")
@Slf4j
public class StateController {

    @Autowired
    StateUpdation stateUpdation;

    @ApiOperation(
            value = "Update State of a Cab",
            notes = "This is used in order to update the state of a Cab"
    )
    @PostMapping(
            value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse updateLocation(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws StateException {

        UpdateStateRequest stateRequest = ClassTransformationUtil.fromString(requestBody, UpdateStateRequest.class);

        boolean isUpdated = false;

        try {
            isUpdated = stateUpdation.updateState(stateRequest);
        }
        catch (StateException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(!isUpdated) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("Current state is same as last update. No updates were made.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("State updated successfully.")
                .build();
    }
}
