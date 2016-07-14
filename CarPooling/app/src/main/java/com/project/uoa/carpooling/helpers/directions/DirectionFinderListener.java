package com.project.uoa.carpooling.helpers.directions;

import com.project.uoa.carpooling.entities.maps.Route;

import java.util.List;

/**
 * Credits to: Mai Thanh Hiep.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
