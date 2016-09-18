package com.project.uoa.carpooling.helpers.directions;

import com.project.uoa.carpooling.entities.maps.Route;

import java.util.List;

/**
 * Listener used with the DirectionFinder class, used to do anything
 * before and after the API HTTP request.
 *
 * Created by Chester Booker and Angel Castro.
 * Adapted from the work of: Mai Thanh Hiep.
 * Code available at: https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
