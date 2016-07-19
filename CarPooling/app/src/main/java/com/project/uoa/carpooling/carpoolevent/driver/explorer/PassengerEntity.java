package com.project.uoa.carpooling.carpoolevent.driver.explorer;

import android.util.Log;

/**
 * Created by Chester on 18/07/2016.
 */
public class PassengerEntity {

    private String ID;
    private String name;
    private String pickupLocation;
    private int passengerCount;

    private boolean isPending;
    private int stopNumber;

    public PassengerEntity(String ID, String name, String pickupLocation, String passengerCount, String isPending) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = Integer.getInteger(passengerCount);
        if(isPending.equals("True")) {
            this.isPending = true;
        }
        else if(isPending.equals("False")) {
            this.isPending = false;
        }
        else {
            Log.e("isPending", "Boolean not set correctly.");
        }
    }

    public PassengerEntity(String ID, String name, String pickupLocation, String passengerCount, int stopNumber) {
        this.ID = ID;
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.passengerCount = Integer.getInteger(passengerCount);
        this.stopNumber = stopNumber;
    }



}
