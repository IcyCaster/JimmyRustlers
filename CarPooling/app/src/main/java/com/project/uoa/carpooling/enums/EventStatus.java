package com.project.uoa.carpooling.enums;

/**
 * An enum to keep track of the user's status for each event.
 * Created by Chester on 18/07/2016.
 */
public enum EventStatus {
    OBSERVER, DRIVER, PASSENGER;

    @Override
    public String toString() {
        switch(this) {
            case OBSERVER: return "Observer";
            case DRIVER: return "Driver";
            case PASSENGER: return "Passenger";
            default: throw new IllegalArgumentException();
        }
    }
}
