package com.project.uoa.carpooling.entities.maps;

/**
 * Entity which represents the distance of a single leg.
 * Used within the Leg entity.
 *
 * Created by Chester Booker and Angel Castro.
 */
public class Distance {
    public String text;
    public int value;

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
