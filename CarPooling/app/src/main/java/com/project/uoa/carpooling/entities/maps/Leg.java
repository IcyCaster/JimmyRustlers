package com.project.uoa.carpooling.entities.maps;

import com.google.android.gms.maps.model.LatLng;

public class Leg {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public String startAddress;
    public LatLng startLocation;
    public LatLng endLocation;
}
