package org.example.cab_management_portal.controllers.location;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.models.dto.UpdateLocationRequest;
import org.example.cab_management_portal.models.response.AppResponse;
import org.example.cab_management_portal.service.location.UpdateLocation;
import org.example.cab_management_portal.utils.ClassTransformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
@Api(value = "cab Location update Base URL's", description = "Contains Api end point to update location of a cab")
@RequestMapping(value = "/v1/location/")
@Slf4j
public class LocationController {

    @Autowired
    UpdateLocation updateLocation;

    @ApiOperation(
            value = "Update location of a Cab",
            notes = "This is used in order to update the location of a Cab"
    )
    @PostMapping(
            value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse updateLocation(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws LocationException {
        UpdateLocationRequest locationRequest = null;

        try {
            locationRequest = ClassTransformationUtil.fromString(requestBody, UpdateLocationRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        boolean isUpdated = false;

        try {
            isUpdated = updateLocation.updateLocation(locationRequest);
        }
        catch (LocationException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(!isUpdated) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("Location is same as last update. No updates were made.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("Location updated successfully.")
                .build();
    }
}
