package com.project.uoa.carpooling.fragments.carpool.passenger.explorer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;
import com.project.uoa.carpooling.helpers.comparators.DriverComparator;

import java.util.ArrayList;
import java.util.Collections;


public class P_E_Offers extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private long requestNumber;
    private ArrayList<DriverEntity> listOfOffersFromDrivers = new ArrayList<>();

    private RecyclerView recyclerView;
    private P_E_OffersRecycler adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;

    private String userID;
    private String eventID;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            PopulateOffers();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        shouldExecuteOnResume = false;

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        PopulateOffers();

        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new P_E_OffersRecycler(listOfOffersFromDrivers, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void fetchTimelineAsync() {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                PopulateOffers();

            }
        });
    }

    public void PopulateOffers() {

        listOfOffersFromDrivers.clear();

        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.child("Offers").exists()) {

                    requestNumber = snapshot.child("Offers").getChildrenCount();

                    for (DataSnapshot requests : snapshot.child("Offers").getChildren()) {

                        if (requests.getValue().equals("Pending")) {

                            fireBaseReference.child("events").child(eventID).child("users").child(requests.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    String driverID = snapshot.getKey();
                                    String driverName = snapshot.child("Name").getValue().toString();

                                    //No location stuff yet
//                                    String pickupName = snapshot.child("PickupName").getValue().toString();
//                                    String pickupLongitude = snapshot.child("PickupLong").getValue().toString();
//                                    String pickupLatitude = snapshot.child("PickupLat").getValue().toString();
//                                    Place pickupLocation = new Place(pickupName, pickupLongitude, pickupLatitude);

                                    String carCapacity = snapshot.child("Passengers").child("PassengerCapacity").getValue().toString();
                                    //TODO: Calculate total space and compare it with number of passengers

                                    String isPending = "True";

                                    // Make driver entity and add it to the list
                                    DriverEntity driver = new DriverEntity(driverID, driverName, isPending, carCapacity);
                                    listOfOffersFromDrivers.add(driver);

                                    callback();

                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Log.e("firebase - error", firebaseError.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    // TODO: Add message to say that there are no requests
                    requestNumber = 0;
                    callback();
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });
    }

    public synchronized void callback() {
        requestNumber--;
        if (requestNumber <= 0) {

            Log.d("T", listOfOffersFromDrivers.toString());

            Collections.sort(listOfOffersFromDrivers, new DriverComparator());
            adapter = new P_E_OffersRecycler(listOfOffersFromDrivers, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
        }
    }
}







