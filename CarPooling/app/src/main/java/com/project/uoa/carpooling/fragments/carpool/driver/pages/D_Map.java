package com.project.uoa.carpooling.fragments.carpool.driver.pages;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;
import com.project.uoa.carpooling.entities.shared.Place;
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
public class D_Map extends MapsFragment {
    private static final String TAG = "D_Map";
    private String driverLocation;
    private String eventLocation;
    private List<String> passengerLocations = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener;

    private DirectionFinderListener listener = this;

    private FirebaseValueEventListener driverFirebaseListener;

    // Push the driver's current location to Firebase when they press the driver button.
    public void pushLocationForPassengers() {
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

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
        // Remove Listener for Driver map, makes sure that they aren't being tracked 24/7/
        // Remove last known current location for further precautions
        Log.d(TAG, "Cleaning up current location tracking");
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").setValue(false);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLocation").setValue(new Place(null, 0, 0));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
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
        pushLocationForPassengers();

        //String intentURI = "https://maps.google.ch/maps?saddr=Manukau&daddr=University of Auckland to:Auckland to: Charles Prevost Dr";
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

    // Updates Firebase when the driver's location changes
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);

            Place currentLocation = new Place(null, loc.getLongitude(), loc.getLatitude());
            fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").setValue(true);
            fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLocation").setValue(currentLocation);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

