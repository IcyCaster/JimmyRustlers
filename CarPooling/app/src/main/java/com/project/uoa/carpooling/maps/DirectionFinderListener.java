package com.project.uoa.carpooling.maps;

import java.util.List;

/**
 * Credits to: Mai Thanh Hiep.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
