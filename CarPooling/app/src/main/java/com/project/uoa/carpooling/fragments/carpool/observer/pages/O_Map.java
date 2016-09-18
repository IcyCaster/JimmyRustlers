package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;

/**
 * Observer fragment for showing location of event.
 *
 * Created by Chester Booker and Angel Castro on 18/07/2016.
 */
public class O_Map extends MapsFragment implements OnMapReadyCallback {
    private static final String TAG = "O_Map";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Get event lat lng pair.
        eventLatLng = getLocationFromAddress(getActivity(), facebookEvent.getLocation());

        // Add Marker and move camera.
        mMap.addMarker(new MarkerOptions()
                .position(eventLatLng)
                .title(facebookEvent.getLocation().toString()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 15));
    }
}