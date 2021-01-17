package org.example.cab_management_portal.controllers.registration;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.dto.RegisterCabRequest;
import org.example.cab_management_portal.models.dto.RegisterCityRequest;
import org.example.cab_management_portal.models.response.AppResponse;
import org.example.cab_management_portal.service.registration.CabRegistration;
import org.example.cab_management_portal.service.registration.CityRegistration;
import org.example.cab_management_portal.utils.ClassTransformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController()
@Api(value = "cab registrations Base URL's", description = "Contains Api end point to register a new cab")
@RequestMapping(value = "/v1/register/")
@Slf4j
public class RegistrationController {

    @Autowired
    CabRegistration cabRegistration;

    @Autowired
    CityRegistration cityRegistration;

    @ApiOperation(
            value = "Register new Cab",
            notes = "This is used in order to create new Cab"
    )
    @PostMapping(
            value = "/cab",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse registerCab(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws RegistrationException {
        RegisterCabRequest cabRequest = null;

        try {
            cabRequest = ClassTransformationUtil.fromString(requestBody, RegisterCabRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        boolean isInserted = false;

        try {
            isInserted = cabRegistration.registerNewCab(cabRequest);
        }
        catch (RegistrationException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(!isInserted) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("Cab with registration number: '" + cabRequest.getRegistrationNumber() + "' already present.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("Cab registered successfully.")
                .build();
    }

    @ApiOperation(
            value = "Register new city",
            notes = "This is used in order to create new city."
    )
    @PostMapping(
            value = "/city",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse registerCity(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws RegistrationException {

        RegisterCityRequest cityRequest = null;

        try {
            cityRequest = ClassTransformationUtil.fromString(requestBody, RegisterCityRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        boolean isInserted = false;

        try {
            isInserted = cityRegistration.registerNewCity(cityRequest);
        }
        catch (RegistrationException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        if(!isInserted) {
            return AppResponse.builder()
                    .statusCode("FAILED")
                    .statusMessage("City already onboarded.")
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("City registered successfully.")
                .build();
    }
}
