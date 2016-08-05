package com.project.uoa.carpooling.fragments.carpool._entities;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.entities.shared.Place;

/**
 * Created by Chester on 18/07/2016.
 */
public class PassengerEntity {

    private String ID;
    private String name = "";
    private Place pickupLocation;
    private int passengerCount;

    private boolean isPending;
    private int stopNumber;

    private DatabaseReference fireBaseReference;

    public PassengerEntity(String ID, String name, Place pickupLocation, String passengerCount, String isPending) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = Integer.parseInt(passengerCount);
        if (isPending.equals("True")) {
            this.isPending = true;
        } else if (isPending.equals("False")) {
            this.isPending = false;
        } else {
            Log.e("isPending", "Boolean not set correctly in constructor. TODO");
        }
    }

    public PassengerEntity(String ID, String name, Place pickupLocation, String passengerCount) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = Integer.parseInt(passengerCount);
    }

    public PassengerEntity(String ID, String name, Place pickupLocation, String passengerCount, int stopNumber) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = Integer.parseInt(passengerCount);
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
