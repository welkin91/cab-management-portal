package org.example.cab_management_portal.core.state_machine.core;

public enum StateMachineType {

    NATIVE_BOOKING("native_booking");

    private final String desc;

    StateMachineType(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
