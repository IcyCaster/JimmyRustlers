package com.project.uoa.carpooling.entities.maps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Entity which represents a leg of a single route.
 * Used within the Route entity.
 *
 * Created by Chester Booker and Angel Castro.
 */
public class Leg {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public String startAddress;
    public LatLng startLocation;
    public LatLng endLocation;
}
