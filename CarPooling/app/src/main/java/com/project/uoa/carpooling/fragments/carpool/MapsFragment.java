package com.project.uoa.carpooling.fragments.carpool;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.helpers.directions.DirectionFinderListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chester on 10/08/2016.
 */
public class MapsFragment extends Fragment implements DirectionFinderListener, OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    protected String eventID;
    protected String userID;
    protected EventStatus eventStatus;
    protected ComplexEventEntity facebookEvent;
    protected String GOOGLE_API_KEY;
    protected LatLng eventLatLng;

    protected String driverLocation;
    protected String eventLocation;
    protected List<String> passengerLocations = new ArrayList<>();

    protected List<Marker> originMarkers = new ArrayList<>();
    protected List<Marker> destinationMarkers = new ArrayList<>();
    protected List<Polyline> polylinePaths = new ArrayList<>();
    protected List<Integer> waypointOrder = new ArrayList<>();

    protected GoogleMap mMap;

    protected MapView mMapView;
    protected Button mStartNavButton;

    // Firebase reference
    protected DatabaseReference fireBaseReference;

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

        // Map Initialization
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // Needed to have map displayed.
        mMapView.getMapAsync(this);

        // Hide drive button if a passenger or observer.
        mStartNavButton = (Button) view.findViewById(R.id.btn_start_nav);
        if (eventStatus == EventStatus.OBSERVER || eventStatus == EventStatus.PASSENGER) {
            mStartNavButton.setVisibility(View.GONE);
        }

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


    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
            originMarkers.clear();
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
            destinationMarkers.clear();
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
            polylinePaths.clear();
        }

        // Clear Map
        mMap.clear();
    }

    @Override
    // Pass optimized order as new argument?
    public void onDirectionFinderSuccess(List<Route> routes) {
        Route route = null;
        if (!routes.isEmpty()) {
            route = routes.get(0);
        }
        if (route != null) {
            Log.d("MapsFragment", "Legs:"+route.legs.size());

            //Save waypoint order.
            waypointOrder = route.waypointOrder;

            if (route.legs.size() <= 1) {
                // Render simple route.
                Leg firstLeg = route.legs.get(0);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLeg.startLocation, 11));

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(firstLeg.startAddress)
                        .position(firstLeg.startLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_start))));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(firstLeg.endAddress)
                        .position(firstLeg.endLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_destination))));
            } else {

                // Render first leg.
                Leg firstLeg = route.legs.get(0);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLeg.startLocation, 11));

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(firstLeg.startAddress)
                        .position(firstLeg.startLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_start))));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(firstLeg.endAddress)
                        .position(firstLeg.endLocation)));

                for (int i = 1; i < route.legs.size() - 1; i++) {
                    Leg leg = route.legs.get(i);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(leg.startLocation, 11));

                    originMarkers.add(mMap.addMarker(new MarkerOptions()
                            .title(leg.startAddress)
                            .position(leg.startLocation)));
                    destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                            .title(leg.endAddress)
                            .position(leg.endLocation)));
                }

                // Render final leg.
                Leg finalLeg = route.legs.get(route.legs.size() - 1);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalLeg.startLocation, 11));

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(finalLeg.startAddress)
                        .position(finalLeg.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(finalLeg.endAddress)
                        .position(finalLeg.endLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_destination))));

//            for (Leg leg : route.legs) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(leg.startLocation, 11));
//
//                originMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .title(leg.startAddress)
//                        .position(leg.startLocation)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_start))));
//                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .title(leg.endAddress)
//                        .position(leg.endLocation)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_maps_destination))));
//            }
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