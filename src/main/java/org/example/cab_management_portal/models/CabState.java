package org.example.cab_management_portal.models;

public enum CabState {

    IDLE("idle"),
    TRIP_ASSIGNED("trip_assigned"),
    ON_TRIP("on_trip");

    private final String desc;

    CabState(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
