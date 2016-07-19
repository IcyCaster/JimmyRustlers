package com.project.uoa.carpooling.entities.explorer;

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


        this.carCapacity = Integer.getInteger(carCapacity);

    }


}
