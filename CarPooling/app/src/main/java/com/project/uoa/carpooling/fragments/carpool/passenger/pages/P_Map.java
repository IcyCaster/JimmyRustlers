package com.project.uoa.carpooling.fragments.carpool.passenger.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;
import com.project.uoa.carpooling.helpers.directions.DirectionFinder;
import com.project.uoa.carpooling.helpers.directions.DirectionFinderListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseChildEventListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.io.UnsupportedEncodingException;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_Map extends MapsFragment {
    private static final String TAG = "P_Map";

    private FirebaseValueEventListener driverRouteFirebaseListener;
    private DirectionFinderListener listener = this;
    private DatabaseReference currentLocationRef;
    private DatabaseReference carpoolPassengersRef;
    private String driverID;

    private Marker driverLocationIcon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

                    // Check if passenger has a specified driver
                    if (!driverID.equals("null")) {

                        Log.d("test3", eventID);

                        final String driverName = dataSnapshot.child(driverID).child("Name").getValue().toString();

                        //Attach valueListener for passenger changes
                        carpoolPassengersRef = fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("Passengers");
                        carpoolPassengersRef.addValueEventListener(driverRouteFirebaseListener = new FirebaseValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                super.onDataChange(dataSnapshot);
                                // Get details of carpool participants
                                fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        super.onDataChange(dataSnapshot);

                                        // Get driver starting location.
                                        DataSnapshot driverDetails = dataSnapshot.child(driverID);

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

                                        //Get event location.
                                        eventLatLng = getLocationFromAddress(getActivity(), facebookEvent.getLocation());
                                        eventLocation = eventLatLng.latitude + "," + eventLatLng.longitude;

                                        Log.d(TAG, "PassengersLocationsFound:" + passengerLocations.size());

                                        // Start request for getting route information.
                                        try {
                                            new DirectionFinder(listener,
                                                    driverLocation,
                                                    eventLocation,
                                                    GOOGLE_API_KEY,
                                                    passengerLocations)
                                                    .execute();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });


                        // Attach valueListener for checking if the driver has begun driving.
                        fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("isDriving").addValueEventListener(new FirebaseValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("test4", eventID);

                                // Detect that the driver is driving, trigger notification and
                                if ((boolean) dataSnapshot.getValue()) {


                                    Log.d("Driver", "Now listening for " + driverName + "'s current location!");
                                    try {
                                        currentLocationRef.removeEventListener(DriverLocationListener);
                                    }
                                    catch(Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
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

    FirebaseChildEventListener DriverLocationListener = new FirebaseChildEventListener() {
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            // Only care about current location changes
            if (dataSnapshot.getKey().equals("CurrentLocation")) {
                // Detect location update, broadcast to map
                Place driverLocation = dataSnapshot.getValue(Place.class);
                Log.d("Broadcast", "Driver is now at: latitude: " + driverLocation.getLatitude() + "; longitude: " + driverLocation.getLongitude());

                //Remove driver lcoation if exists
                if (driverLocationIcon != null) {
                    driverLocationIcon.remove();
                }

                //Draw driver location on map
                Log.d(TAG, "Driver Location Updated");
                LatLng driverLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
                driverLocationIcon = mMap.addMarker(new MarkerOptions()
                        .position(driverLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_car)));
            }
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

        if (carpoolPassengersRef != null && driverRouteFirebaseListener != null) {
            carpoolPassengersRef.removeEventListener(driverRouteFirebaseListener);
        }
    }
}
