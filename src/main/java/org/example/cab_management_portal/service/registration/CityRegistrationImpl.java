package org.example.cab_management_portal.service.registration;

import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.dto.RegisterCityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityRegistrationImpl implements CityRegistration {

    @Autowired
    ServiceStorage serviceStorage;

    @Override
    public boolean registerNewCity(RegisterCityRequest cityRequest) throws RegistrationException {
        if(cityRequest == null || cityRequest.getCityName() == null || cityRequest.getCityName().length() == 0) {
            throw new RegistrationException("City Name not present.");
        }

        return serviceStorage.registerCity(cityRequest.getCityName().toUpperCase());
    }
}
