package com.project.uoa.carpooling.fragments.carpool._entities;

import com.project.uoa.carpooling.entities.shared.Place;

/**
 * PassengerEntity is an object used when displaying the user's with an overview of a particular passenger in the carpoolevent
 * Created by Chester and Angel on 18/07/2016.
 */
public class PassengerEntity {

    private String ID;
    private String name = "";
    private Place pickupLocation;
    private int passengerCount;

    private boolean isPending;
    private int stopNumber;

    public PassengerEntity(String ID, String name, Place pickupLocation, int passengerCount, boolean isPending) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = passengerCount;
        this.isPending = isPending;
    }

    public PassengerEntity(String ID, String name, Place pickupLocation, int passengerCount) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = passengerCount;
    }

    public PassengerEntity(String ID, String name, Place pickupLocation, int passengerCount, int stopNumber) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = passengerCount;
        this.stopNumber = stopNumber;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Place getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Place pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }
}
