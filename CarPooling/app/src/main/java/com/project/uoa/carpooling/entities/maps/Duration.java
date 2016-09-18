package com.project.uoa.carpooling.entities.maps;

/**
 * Entity which represents a route. Used to hold data from
 * any sent Google Maps Directions API HTTP requests.
 *
 * Created by Chester Booker and Angel Castro.
 * Adapted from the work of: Mai Thanh Hiep.
 * Code available at: https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */
public class Duration {
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
