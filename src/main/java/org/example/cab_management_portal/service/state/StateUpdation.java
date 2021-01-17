package org.example.cab_management_portal.service.state;

import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.models.dto.UpdateStateRequest;

public interface StateUpdation {

    public boolean updateState(UpdateStateRequest stateRequest) throws StateException;
}
