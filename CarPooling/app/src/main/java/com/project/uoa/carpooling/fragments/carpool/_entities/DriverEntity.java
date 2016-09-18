package com.project.uoa.carpooling.fragments.carpool._entities;

/**
 * DriverEntity is an object used when displaying the user's with an overview of a particular driver in the carpoolevent
 * Created by Chester and Angel on 18/07/2016.
 */
public class DriverEntity {

    private String ID;
    private String name;
    private boolean isPending;
    private int carCapacity;

    public DriverEntity(String ID, String name, boolean isPending, int carCapacity ) {
        this.ID = ID;
        this.name = name;
        this.isPending = isPending;
        this.carCapacity = carCapacity;
    }

    public DriverEntity(String ID, String name, int carCapacity ) {
        this.ID = ID;
        this.name = name;
        this.carCapacity = carCapacity;
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
