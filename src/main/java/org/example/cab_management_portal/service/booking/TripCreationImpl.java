package org.example.cab_management_portal.service.booking;

import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dto.BookingRequest;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripCreationImpl implements TripCreation {

    @Autowired
    ServiceStorage serviceStorage;

    @Override
    public Cab bookCab(BookingRequest bookingRequest) throws TripException {
        if(CommonUtils.isEmpty(bookingRequest.getCityId())) {
            throw new TripException("City can not be empty.");
        }

        return serviceStorage.bookIdleCab(bookingRequest.getCityId().toUpperCase());
    }
}
