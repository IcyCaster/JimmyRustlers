package com.project.uoa.carpooling.fragments.carpool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;

/**
 * Created by Chester on 10/08/2016.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private String eventID;
    private String userID;
    private EventStatus eventStatus;

    public Place eventLocation;
    public String GOOGLE_API_KEY;
    public MapView mMapView;

    // Firebase reference
    private DatabaseReference fireBaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();
        eventLocation = ((CarpoolEventActivity)getActivity()).getEventLocation();
        eventStatus = ((CarpoolEventActivity)getActivity()).getEventStatus();

        GOOGLE_API_KEY = getActivity().getResources().getString(R.string.google_api_key);
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_observer_map, container, false);

        // Map Initialization
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // Needed to have map displayed.
        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { }
}