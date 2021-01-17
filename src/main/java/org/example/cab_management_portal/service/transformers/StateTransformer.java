package org.example.cab_management_portal.service.transformers;

import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.StatusUpdate;
import org.example.cab_management_portal.models.dto.UpdateStateRequest;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class StateTransformer {

    public StatusUpdate transformState(UpdateStateRequest stateRequest) throws TransformationException {
        if(stateRequest == null) {
            throw new TransformationException("State request object is empty.");
        }

        if(CommonUtils.isEmpty(stateRequest.getRegistrationNumber())) {
            throw new TransformationException("Registration number is not present.");
        }

        if(stateRequest.getState() == null) {
            throw new TransformationException("State can not be empty.");
        }

        return StatusUpdate.builder()
                .registrationNumber(stateRequest.getRegistrationNumber())
                .state(stateRequest.getState())
                .build();
    }
}
