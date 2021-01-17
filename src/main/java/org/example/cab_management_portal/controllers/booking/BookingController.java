package org.example.cab_management_portal.controllers.booking;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dto.BookingRequest;
import org.example.cab_management_portal.models.response.AppResponse;
import org.example.cab_management_portal.service.booking.TripCreation;
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
@RequestMapping(value = "/v1/trips/")
@Slf4j
public class BookingController {

    @Autowired
    TripCreation tripCreation;

    @ApiOperation(
            value = "Book a Cab",
            notes = "Get booking for most IDLE cab"
    )
    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AppResponse bookCab(
            @RequestBody String requestBody,
            HttpServletRequest request
    ) throws TripException {
        BookingRequest bookingRequest = null;

        try {
            bookingRequest = ClassTransformationUtil.fromString(requestBody, BookingRequest.class);
        }
        catch (Exception e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        Cab bookedCab = null;

        try {
            bookedCab = tripCreation.bookCab(bookingRequest);
        }
        catch (TripException e) {
            return AppResponse.builder()
                    .statusCode("ERROR")
                    .statusMessage(e.getMessage())
                    .build();
        }

        return AppResponse.builder()
                .statusCode("SUCCESS")
                .statusMessage("Booking successfully.")
                .data(bookedCab)
                .build();
    }
}
