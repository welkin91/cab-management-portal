package org.example.cab_management_portal.service.registration;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dto.RegisterCabRequest;
import org.example.cab_management_portal.service.transformers.CabTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CabRegistrationImpl implements CabRegistration {

    @Autowired
    ServiceStorage serviceStorage;

    @Autowired
    CabTransformer cabTransformer;

    @Override
    public boolean registerNewCab(RegisterCabRequest cabRequest) throws RegistrationException {
        Cab cab = null;
        try {
            cab = cabTransformer.transformIntoCab(cabRequest);
        }
        catch (TransformationException exception) {
            log.error("Error while transforming car request. error: {}", exception.getMessage());
            throw new RegistrationException(exception.getMessage());
        }

        return serviceStorage.registerCab(cab);
    }
}
