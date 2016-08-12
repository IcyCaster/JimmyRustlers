package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_Map extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EVENT_ID = "param1";
    private static final String TAG = "Event_Map";
    private String GOOGLE_API_KEY;

    // TODO: Rename and change types of parameters
    private String eventID;
    private String userID;
    private String eventStatus;

    // Routing components
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    //TODO Update string with actual address dynamically.
    private String mSelectedAddress = "University Of Auckland";
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;

    private MapView mMapView;

    // Firebase reference
    private DatabaseReference fireBaseReference;
    private String mEventLocation = "University Of Auckland";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventStatus = ((CarpoolEventActivity)getActivity()).getEventStatus().toString();
        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();
        GOOGLE_API_KEY = getActivity().getResources().getString(R.string.google_api_key);

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_observer_map, container, false);

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

        Place eventLocation = ((CarpoolEventActivity)getActivity()).getFacebookEvent().getLocation();
        LatLng eventLatLng = getLocationFromAddress(getActivity(), eventLocation);

        // Add Marker and move camera.
        mMap.addMarker(new MarkerOptions()
                .position(eventLatLng)
                .title(eventLocation.toString()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 15));
    }

    public LatLng getLocationFromAddress(Context context, Place eventLocation) {
        // Check if Facebook Object has valid LatLng.
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
