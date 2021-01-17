package org.example.cab_management_portal.service.registration;

import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.dto.RegisterCityRequest;

public interface CityRegistration {

    public boolean registerNewCity(RegisterCityRequest cityRequest) throws RegistrationException;
}
