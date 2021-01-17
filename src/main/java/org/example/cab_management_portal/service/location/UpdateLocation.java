package org.example.cab_management_portal.service.location;

import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.models.dto.UpdateLocationRequest;

public interface UpdateLocation {

    public boolean updateLocation(UpdateLocationRequest locationRequest) throws LocationException;
}
