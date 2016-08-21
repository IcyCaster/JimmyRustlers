package com.project.uoa.carpooling.entities.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    public List<Leg> legs;
    public List<LatLng> points;
    public List<Integer> waypointOrder;
}
