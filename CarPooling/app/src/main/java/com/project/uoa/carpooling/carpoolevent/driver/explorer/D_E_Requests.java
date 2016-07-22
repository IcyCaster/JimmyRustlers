package com.project.uoa.carpooling.carpoolevent.driver.explorer;

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
import com.project.uoa.carpooling.carpoolevent._entities.PassengerEntity;
import com.project.uoa.carpooling.entities.facebook.Place;
import com.project.uoa.carpooling.helpers.PassengerComparator;

import java.util.ArrayList;
import java.util.Collections;


public class D_E_Requests extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private long requestNumber;
    private ArrayList<PassengerEntity> listOfRequestingPassenger = new ArrayList<>();

    private RecyclerView recyclerView;
    private D_E_RequestsRecycler adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;

    private String userID;
    private String eventID;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            PopulateRequests();
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

        PopulateRequests();

        view = inflater.inflate(R.layout.carpool_driver_exp_offers, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new D_E_RequestsRecycler(listOfRequestingPassenger, getActivity());

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
                PopulateRequests();

            }
        });
    }

    public void PopulateRequests() {

        listOfRequestingPassenger.clear();

        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.child("Requests").exists()) {

                    requestNumber = snapshot.child("Requests").getChildrenCount();
                    for (DataSnapshot requests : snapshot.child("Requests").getChildren()) {

                        if (requests.getValue().equals("Pending")) {

                            fireBaseReference.child("events").child(eventID).child("users").child(requests.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    String passengerID = snapshot.getKey();
                                    String passengerName = snapshot.child("Name").getValue().toString();
                                    String pickupName = snapshot.child("PickupName").getValue().toString();
                                    String pickupLongitude = snapshot.child("PickupLong").getValue().toString();
                                    String pickupLatitude = snapshot.child("PickupLat").getValue().toString();

                                    Place pickupLocation = new Place(pickupName, pickupLongitude, pickupLatitude);

                                    String passengerCount = snapshot.child("PassengerCount").getValue().toString();

                                    String isPending = "True";

                                    // Make passenger entity and add it to the list
                                    PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount, isPending);
                                    listOfRequestingPassenger.add(passenger);

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
        if (requestNumber > 0) {

            Log.d("T", listOfRequestingPassenger.toString());

            Collections.sort(listOfRequestingPassenger, new PassengerComparator());
            adapter = new D_E_RequestsRecycler(listOfRequestingPassenger, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
        }
    }
}







