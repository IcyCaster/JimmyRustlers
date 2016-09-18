package com.project.uoa.carpooling.entities.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Entity which represents a route. Used to hold data from
 * any sent Google Maps Directions API HTTP requests.
 *
 * Created by Chester Booker and Angel Castro.
 * Adapted from the work of: Mai Thanh Hiep.
 * Code available at: https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */
public class Route {
    public List<Leg> legs;
    public List<LatLng> points;
    public List<Integer> waypointOrder;
}
