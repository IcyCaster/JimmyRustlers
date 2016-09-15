package com.project.uoa.carpooling.fragments.carpool.driver.pages;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;
import com.project.uoa.carpooling.helpers.directions.DirectionFinder;
import com.project.uoa.carpooling.helpers.directions.DirectionFinderListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class D_Map extends MapsFragment {
    private static final String TAG = "D_Map";

    private DirectionFinderListener listener = this;

    private FirebaseValueEventListener driverFirebaseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mStartNavButton.setEnabled(false);
        mStartNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGoogleMapsNavigationIntent();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove Listener for Driver map.
        fireBaseReference.child("events").child(eventID).child("users").removeEventListener(driverFirebaseListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        displayDriverRoute();
    }

    private void displayDriverRoute() {
        driverFirebaseListener = new FirebaseValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if current user is a driver.
                if (dataSnapshot.child(userID).child("Status").getValue() != null) {
                    Log.d(TAG, "Status:" + dataSnapshot.child(userID).child("Status").getValue().toString());
                    if (dataSnapshot.child(userID).child("Status").getValue().toString().equals("Driver")) {
                        try {
                            /*
                            TODO - Get positions (Latlng or address) of all passengers and driver.
                            TODO - Then save that data for both the map and drive intent later on.
                            TODO - Use that data to populate optimized route on map.
                            TODO - Can probably use this logic for passenger side as well.
                             */

                            // Get driver starting location.
                            DataSnapshot driverDetails = dataSnapshot.child(userID);

                            if (driverDetails.hasChild("StartLocation") && driverDetails.hasChild("Passengers")) {

                                String startDriverLat = driverDetails.child("StartLocation").child("latitude").getValue().toString();
                                String startDriverLng = driverDetails.child("StartLocation").child("longitude").getValue().toString();
                                driverLocation = startDriverLat + "," + startDriverLng;

                                // Get Driver's passenger locations, if available.
                                passengerLocations.clear();

                                for (DataSnapshot passengerID : driverDetails.child("Passengers").getChildren()) {
                                    if (passengerID.getKey().toString().equals("PassengerCapacity")) {
                                        continue;
                                    }
                                    Log.d(TAG, passengerID.getKey().toString());
                                    DataSnapshot passengerDetails = dataSnapshot.child(passengerID.getKey().toString());
                                    String passengerLat = passengerDetails.child("PickupLocation").child("latitude").getValue().toString();
                                    String passengerLng = passengerDetails.child("PickupLocation").child("longitude").getValue().toString();
                                    passengerLocations.add(passengerLat + "," + passengerLng);
                                }
                            }

                            // Get event location.
                            eventLatLng = getLocationFromAddress(getActivity(), facebookEvent.getLocation());
                            eventLocation = eventLatLng.latitude + "," + eventLatLng.longitude;

                            Log.d(TAG, "PassengersLocationsFound:" + passengerLocations.size());

                            // Start request for getting route information.
                            new DirectionFinder(listener,
                                    driverLocation,
                                    eventLocation,
                                    GOOGLE_API_KEY,
                                    passengerLocations)
                                    .execute();

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        // Safe to enable drive navigation button now.
                        mStartNavButton.setEnabled(true);
                    }
                }
            }
        };

        fireBaseReference.child("events").child(eventID).child("users").addValueEventListener(driverFirebaseListener);
    }

    private void launchGoogleMapsNavigationIntent() {
        String intentURI = "https://maps.google.ch/maps?" +
                "saddr=" +
                driverLocation +
                "&daddr=";

        if (passengerLocations != null && waypointOrder != null) {
            for (int i : waypointOrder) {
                intentURI += passengerLocations.get(i) + " to:";
            }
        }

        intentURI += eventLocation;

        Uri gmmIntentUri = Uri.parse(intentURI);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }


    @Override
    // Pass optimized order as new argument?
    public void onDirectionFinderSuccess(List<Route> routes) {
        Route route = null;
        if (!routes.isEmpty()) {
            route = routes.get(0);
        }
        if (route != null) {
            //Save waypoint order.
            waypointOrder = route.waypointOrder;

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
