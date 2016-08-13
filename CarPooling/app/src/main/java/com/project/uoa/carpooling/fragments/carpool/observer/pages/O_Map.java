package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.fragments.carpool.MapsFragment;

import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_Map extends MapsFragment implements OnMapReadyCallback {
    private static final String TAG = "O_Map";

    private GoogleMap mMap;
    private LatLng eventLatLng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Ask for permission, and handle if not granted.
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        // Get event lat lng pair.
        eventLatLng = getLocationFromAddress(getActivity(), eventLocation);

        // Add Marker and move camera.
        mMap.addMarker(new MarkerOptions()
                .position(eventLatLng)
                .title(eventLocation.toString()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 15));
    }

    public LatLng getLocationFromAddress(Context context, Place eventLocation) {
        Log.d(TAG, "Placename: " + eventLocation.toString());
        Log.d(TAG, "Placelat: " + eventLocation.getLatitude());
        Log.d(TAG, "Placelng: " + eventLocation.getLongitude());

        // Check if Facebook Object has valid LatLng, else geocode it.
        if (eventLocation.hasLatLong()){
            return new LatLng(eventLocation.getLatitude(), eventLocation.getLongitude());

        } else {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            LatLng eventLatLng = null;

            try {
                address = coder.getFromLocationName(eventLocation.toString(), 5);
                if (address == null) {
                    return null;
                }

                Address location = address.get(0);
                eventLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return eventLatLng;
        }
    }
}
