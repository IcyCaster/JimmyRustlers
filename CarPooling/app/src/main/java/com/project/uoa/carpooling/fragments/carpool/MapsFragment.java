package com.project.uoa.carpooling.fragments.carpool;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;

import java.util.List;

/**
 * Created by Chester on 10/08/2016.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    public String eventID;
    public String userID;
    public EventStatus eventStatus;
    public ComplexEventEntity facebookEvent;
    public String GOOGLE_API_KEY;
    public LatLng eventLatLng;

    public GoogleMap mMap;

    public MapView mMapView;
    public Button mStartNavButton;

    // Firebase reference
    public DatabaseReference fireBaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();
        eventStatus = ((CarpoolEventActivity)getActivity()).getEventStatus();
        facebookEvent = ((CarpoolEventActivity)getActivity()).getFacebookEvent();

        GOOGLE_API_KEY = getActivity().getResources().getString(R.string.google_api_key);
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_event_map, container, false);

        // Hide drive button if a passenger or observer.
        mStartNavButton = (Button) view.findViewById(R.id.btn_start_nav);
        if (eventStatus == EventStatus.OBSERVER || eventStatus == EventStatus.PASSENGER) {
            mStartNavButton.setVisibility(View.GONE);
        }

        // Map Initialization
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // Needed to have map displayed.
        mMapView.getMapAsync(this);

        return view;
    }

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
    }

    public LatLng getLocationFromAddress(Context context, Place eventLocation) {
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