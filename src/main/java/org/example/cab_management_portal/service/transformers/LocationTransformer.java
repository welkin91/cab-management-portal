package org.example.cab_management_portal.service.transformers;

import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.LocationUpdate;
import org.example.cab_management_portal.models.dto.UpdateLocationRequest;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class LocationTransformer {

    public LocationUpdate transformLocation(UpdateLocationRequest locationRequest) throws TransformationException {
        if(locationRequest == null) {
            throw new TransformationException("Received empty location update request.");
        }

        if(CommonUtils.isEmpty(locationRequest.getRegistrationNumber())) {
            throw new TransformationException("Registration number not present in location update.");
        }

        if(CommonUtils.isEmpty(locationRequest.getLocationCity())) {
            throw new TransformationException("Location not present in location update.");
        }

        return LocationUpdate.builder()
                .registrationNumber(locationRequest.getRegistrationNumber())
                .locationCity(locationRequest.getLocationCity().toUpperCase())
                .build();
    }
}
