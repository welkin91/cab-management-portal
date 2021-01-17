package org.example.cab_management_portal.core.storage.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.AnalyticsStorage;
import org.example.cab_management_portal.core.state_machine.core.impl.CabStateMachine;
import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.StatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class StateHelperImpl implements StateHelper {

    @Autowired
    CabStateMachine stateMachine;

    @Autowired
    AnalyticsStorage analyticsStorage;

    @Override
    public boolean updateCabState(
            Map<String, PriorityBlockingQueue<Cab>> _cityLevelIdleCabs,
            Map<String, Cab> _allCabs,
            StatusUpdate statusUpdate
    ) throws StateException {
        /**
         * Check if cab exists. else, throw an exception.
         */
        if(!_allCabs.containsKey(statusUpdate.getRegistrationNumber())) {
            throw new StateException("Cab with registration number: '" + statusUpdate.getRegistrationNumber() + "' does not exist.");
        }

        Cab cab = _allCabs.get(statusUpdate.getRegistrationNumber());
        String location = cab.getCity();

        /**
         * return false if the state is same as previous.
         */
        if(cab.getState() == statusUpdate.getState()) {
            return false;
        }

        /**
         * validate if the jump from previous state and current input state is present or not.
         */
        boolean hopAllowed = stateMachine.validate(cab.getState(), statusUpdate.getState());

        /**
         * If not, throw and exception.
         */
        if(!hopAllowed) {
            throw new StateException("State change from '" + cab.getState() + "' to '" + statusUpdate.getState() + "' is not allowed.");
        }

        /**
         * if previous state was IDLE, remove it from the IDLE cabs
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(location).remove(cab);
        }

        cab.setState(statusUpdate.getState());
        cab.setLastUpdatedAt(System.currentTimeMillis());

        _allCabs.put(statusUpdate.getRegistrationNumber(), cab);

        /**
         * Pass current updated state of a cab to analytics.
         */
        analyticsStorage.updateCabStatus(cab);

        /**
         * if new state is IDLE, add it back to the IDLE cabs set.
         */
        if(cab.getState().equals(CabState.IDLE)) {
            _cityLevelIdleCabs.get(cab.getCity()).offer(cab);
        }

        return true;
    }
}
