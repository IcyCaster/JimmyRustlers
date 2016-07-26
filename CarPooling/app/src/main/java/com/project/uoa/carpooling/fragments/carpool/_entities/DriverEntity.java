package com.project.uoa.carpooling.fragments.carpool._entities;

import android.util.Log;

/**
 * Created by Chester on 18/07/2016.
 */
public class DriverEntity {

    private String ID;
    private String name;
    private boolean isPending;
    private int carCapacity;

    public DriverEntity(String ID, String name, String isPending, String carCapacity ) {
        this.ID = ID;
        this.name = name;
        if(isPending.equals("True")) {
            this.isPending = true;
        }
        else if(isPending.equals("False")) {
            this.isPending = false;
        }
        else {
            Log.e("isPending", "Boolean not set correctly.");
        }

        this.carCapacity = Integer.parseInt(carCapacity);

    }

    public DriverEntity(String ID, String name, String carCapacity ) {
        this.ID = ID;
        this.name = name;
        this.carCapacity = Integer.parseInt(carCapacity);
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

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public void setCarCapacity(int carCapacity) {
        this.carCapacity = carCapacity;
    }
}
