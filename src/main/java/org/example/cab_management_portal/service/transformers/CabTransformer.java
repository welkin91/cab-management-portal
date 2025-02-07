package org.example.cab_management_portal.service.transformers;

import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.models.dto.RegisterCabRequest;
import org.springframework.stereotype.Component;

@Component
public class CabTransformer {

    public Cab transformIntoCab(RegisterCabRequest cabRequest) throws TransformationException {
        if(cabRequest == null) {
            return null;
        }

        if(cabRequest.getCityId() == null || cabRequest.getCityId().length() == 0) {
            throw new TransformationException("city id not present");
        }

        if(cabRequest.getRegistrationNumber() == null || cabRequest.getRegistrationNumber().length() == 0) {
            throw new TransformationException("registration number not present");
        }

        if(cabRequest.getState() == null) {
            throw new TransformationException("vehicle state not present");
        }

        return Cab.builder()
                .registrationNumber(cabRequest.getRegistrationNumber())
                .state(cabRequest.getState())
                .city(cabRequest.getCityId().toUpperCase())
                .lastUpdatedAt(System.currentTimeMillis())
                .build();
    }

    public CabEntry transformIntoEntry(Cab cab, CabState state) throws TransformationException {
        if(cab == null) {
            return null;
        }

        if(cab.getRegistrationNumber() == null || cab.getRegistrationNumber().length() == 0) {
            throw new TransformationException("registration number not present");
        }

        if(state == null) {
            throw new TransformationException("vehicle state not present");
        }

        return CabEntry.builder()
                .registrationNumber(cab.getRegistrationNumber())
                .state(state)
                .lastUpdatedAt(System.currentTimeMillis())
                .build();
    }
}
