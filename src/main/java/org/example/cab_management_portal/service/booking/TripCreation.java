package org.example.cab_management_portal.service.booking;

import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dto.BookingRequest;

public interface TripCreation {

    public Cab bookCab(BookingRequest bookingRequest) throws TripException;
}
