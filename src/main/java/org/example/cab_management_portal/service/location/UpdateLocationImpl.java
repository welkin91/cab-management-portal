package org.example.cab_management_portal.service.location;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.LocationUpdate;
import org.example.cab_management_portal.models.dto.UpdateLocationRequest;
import org.example.cab_management_portal.service.transformers.LocationTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateLocationImpl implements UpdateLocation {

    @Autowired
    LocationTransformer locationTransformer;

    @Autowired
    ServiceStorage serviceStorage;

    @Override
    public boolean updateLocation(UpdateLocationRequest locationRequest) throws LocationException {

        LocationUpdate locationUpdate = null;
        try {
            locationUpdate = locationTransformer.transformLocation(locationRequest);
        }
        catch (TransformationException e) {
            log.error("Transformation error occurred while transforming location update. error: {}", e.getMessage());
            throw new LocationException(e.getMessage());
        }

        return serviceStorage.updateLocation(locationUpdate);
    }
}
