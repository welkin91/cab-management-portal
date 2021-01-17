package org.example.cab_management_portal.core.state_machine.core.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.state_machine.core.ActionType;
import org.example.cab_management_portal.core.state_machine.core.State;
import org.example.cab_management_portal.core.state_machine.core.StateMachine;
import org.example.cab_management_portal.core.state_machine.core.StateMachineType;
import org.example.cab_management_portal.models.CabState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CabStateMachine implements StateMachineImplementation {

    private static StateMachine _stateMachine;

    @Override
    public StateMachine configure() {
        if(_stateMachine != null) {
            return _stateMachine;
        }

        Set<ActionType> commonActions = new HashSet<>();
        commonActions.add(ActionType.STATE_JUMP);

        State idle = State.builder()
                .state(CabState.IDLE)
                .actions(commonActions)
                .nextState(CabState.TRIP_ASSIGNED)
                .build();

        State tripAssigned = State.builder()
                .state(CabState.TRIP_ASSIGNED)
                .actions(commonActions)
                .nextState(CabState.ON_TRIP)
                .build();

        State onTrip = State.builder()
                .state(CabState.ON_TRIP)
                .actions(commonActions)
                .nextState(CabState.IDLE)
                .build();

        List<State> stateSequence = new ArrayList<>();
        stateSequence.add(idle);
        stateSequence.add(tripAssigned);
        stateSequence.add(onTrip);

        _stateMachine = StateMachine.builder()
                .type(StateMachineType.NATIVE_BOOKING)
                .stateSequence(stateSequence)
                .build();

        return _stateMachine;
    }

    @Override
    public boolean validate(CabState stateA, CabState stateB) {
        State currentState = null;
        for(State state: _stateMachine.getStateSequence()) {
            if(state.getState().equals(stateA)) {
                currentState = state;
                break;
            }
        }

        if(currentState == null) {
            return false;
        }

        return stateB == currentState.getNextState();
    }

    @Override
    public CabState getNextState(CabState cabState) {
        State currentState = null;
        for(State state: _stateMachine.getStateSequence()) {
            if(state.getState().equals(cabState)) {
                currentState = state;
                break;
            }
        }

        if(currentState == null) {
            return null;
        }

        return currentState.getNextState();
    }
}
