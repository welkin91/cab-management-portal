package org.example.cab_management_portal.service.state;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.StatusUpdate;
import org.example.cab_management_portal.models.dto.UpdateStateRequest;
import org.example.cab_management_portal.service.transformers.StateTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StateUpdationImpl implements StateUpdation {

    @Autowired
    StateTransformer stateTransformer;

    @Autowired
    ServiceStorage serviceStorage;

    @Override
    public boolean updateState(UpdateStateRequest stateRequest) throws StateException {
        StatusUpdate statusUpdate = null;

        try {
            statusUpdate = stateTransformer.transformState(stateRequest);
        }
        catch (TransformationException e) {
            log.error("Error occurred while transforming state request object. error: {}", e.getMessage());
            throw new StateException(e.getMessage());
        }

        return serviceStorage.updateCabState(statusUpdate);
    }
}
