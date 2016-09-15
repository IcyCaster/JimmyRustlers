package com.project.uoa.carpooling.fragments.carpool.passenger.pages;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;
import com.project.uoa.carpooling.helpers.directions.DirectionFinder;
import com.project.uoa.carpooling.helpers.directions.DirectionFinderListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseChildEventListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_Map extends MapsFragment {
    private static final String TAG = "P_Map";

    private DirectionFinderListener listener = this;
    private DatabaseReference currentLocationRef;
    private String driverID;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        displayPassengerRoute();
    }

    private void displayPassengerRoute() {
        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // Get passenger starting location.
                    String startLat = dataSnapshot.child("PickupLocation").child("latitude").getValue().toString();
                    String startLng = dataSnapshot.child("PickupLocation").child("longitude").getValue().toString();

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

    public void monitorDriverCurrentLocation() {

        Log.d("test1", eventID);

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("test2", eventID);

                // Check if they are a passenger for this event
                if (dataSnapshot.child(userID).child("Status").getValue().toString().equals("Passenger")) {

                    Log.d("test2.5", eventID);

                    driverID = dataSnapshot.child(userID).child("Driver").getValue().toString();
                    currentLocationRef = fireBaseReference.child("events").child(eventID).child("users").child(driverID);

                    // Check if they have a specified driver
                    if (!driverID.equals("null")) {

                        Log.d("test3", eventID);

                        final String driverName = dataSnapshot.child(driverID).child("Name").getValue().toString();

                        // Attach valueListener
                        fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("isDriving").addValueEventListener(new FirebaseValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("test4", eventID);

                                // Detect that the driver is driving, trigger notification and
                                if ((boolean) dataSnapshot.getValue()) {


                                    Log.d("Driver", "Now listening for " + driverName + "'s current location!");
                                    currentLocationRef.addChildEventListener(DriverLocationListener);

                                } else {

                                    // Detach as the driver is no longer driving
                                    currentLocationRef.removeEventListener(DriverLocationListener);

                                }
                            }

                        });
                    }
                }
            }
        });
    }

    static FirebaseChildEventListener DriverLocationListener = new FirebaseChildEventListener() {
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            // Only care about current location changes
            if (dataSnapshot.getKey().equals("CurrentLocation")) {
                // Detect location update, broadcast to map
                Place driverLocation = dataSnapshot.getValue(Place.class);
                Log.d("Broadcast", "Driver is now at: latitude: " + driverLocation.getLatitude() + "; longitude: " + driverLocation.getLongitude());

                //TODO
                Log.d("TODO ANGEL", "Update Map HERE!");
            }
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            // Do nothing...
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        monitorDriverCurrentLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentLocationRef.removeEventListener(DriverLocationListener);
    }

//    @Override
//    public void onDirectionFinderSuccess(List<Route> routes) {
//        for (Route route : routes) {
//            for (Leg leg : route.legs) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(leg.startLocation, 11));
//
//                originMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .title(leg.startAddress)
//                        .position(leg.startLocation)));
//                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .title(leg.endAddress)
//                        .position(leg.endLocation)));
//            }
//            // Options specify line graphic details and path of line.
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.rgb(101, 156, 239));
//
//            for (int i = 0; i < route.points.size(); i++)
//                polylineOptions.add(route.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//        }
//    }
}
