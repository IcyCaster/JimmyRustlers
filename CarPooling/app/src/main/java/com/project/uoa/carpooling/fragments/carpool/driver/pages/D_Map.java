package com.project.uoa.carpooling.fragments.carpool.driver.pages;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;
import com.project.uoa.carpooling.helpers.directions.DirectionFinder;
import com.project.uoa.carpooling.helpers.directions.DirectionFinderListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class D_Map extends MapsFragment implements DirectionFinderListener {
    private static final String TAG = "D_Map";

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    private DirectionFinderListener listener = this;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        displayDriverRoute();
    }

    private void displayDriverRoute() {
        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    DataSnapshot driverDetails = dataSnapshot.child(userID);

                    // Get passenger starting location.
                    String startLat = driverDetails.child("StartLocation").child("latitude").getValue().toString();
                    String startLng = driverDetails.child("StartLocation").child("longitude").getValue().toString();

                    // Get event location.
                    eventLatLng = getLocationFromAddress(getActivity(), facebookEvent.getLocation());

                    // Start request for getting route information.
                    new DirectionFinder(listener,
                            startLat + "," + startLng,
                            eventLatLng.latitude + "," + eventLatLng.longitude,
                            GOOGLE_API_KEY)
                            .execute();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            for (Leg leg : route.legs) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(leg.startLocation, 11));

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(leg.startAddress)
                        .position(leg.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(leg.endAddress)
                        .position(leg.endLocation)));
            }
            // Options specify line graphic details and path of line.
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(101, 156, 239));

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
