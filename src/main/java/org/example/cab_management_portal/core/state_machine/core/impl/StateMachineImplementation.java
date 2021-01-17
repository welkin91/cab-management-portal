package org.example.cab_management_portal.core.state_machine.core.impl;

import org.example.cab_management_portal.core.state_machine.core.StateMachine;
import org.example.cab_management_portal.models.CabState;

public interface StateMachineImplementation {

    public StateMachine configure();

    public boolean validate(CabState stateA, CabState stateB);

    public CabState getNextState(CabState state);
}
