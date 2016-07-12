package com.project.uoa.carpooling.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Credits to: Mai Thanh Hiep.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
