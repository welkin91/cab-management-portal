package org.example.cab_management_portal.service.registration;

import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.models.dto.RegisterCabRequest;

public interface CabRegistration {

    public boolean registerNewCab(RegisterCabRequest cabRequest) throws RegistrationException;

}
