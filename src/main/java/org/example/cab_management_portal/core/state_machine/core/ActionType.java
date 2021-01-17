package org.example.cab_management_portal.core.state_machine.core;

public enum ActionType {

    STATE_JUMP("state_jump");

    private final String desc;

    ActionType(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
