package com.project.uoa.carpooling.fragments.carpool.driver.explorer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.fragments.carpool._entities.PassengerEntity;
import com.project.uoa.carpooling.helpers.comparators.PassengerComparator;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

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
    private TextView noOffersText;

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



        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new D_E_RequestsRecycler(listOfRequestingPassenger, getActivity());
        noOffersText = (TextView) view.findViewById(R.id.emptylist_text);
        noOffersText.setText("No Passengers Available!");


        PopulateRequests();

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

        noOffersText.setVisibility(View.GONE);

        listOfRequestingPassenger.clear();

        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.child("Requests").exists()) {

                    requestNumber = snapshot.child("Requests").getChildrenCount();

                    for (DataSnapshot requests : snapshot.child("Requests").getChildren()) {

                        if (requests.getValue().equals("Pending")) {

                            fireBaseReference.child("events").child(eventID).child("users").child(requests.getKey()).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    // Only display Passengers who are public
                                    if (snapshot.child("Status").getValue().equals("Passenger") && (boolean) snapshot.child("isPublic").getValue()) {

                                        String passengerID = snapshot.getKey();
                                        String passengerName = snapshot.child("Name").getValue().toString();

                                        Place pickupLocation = snapshot.child("PickupLocation").getValue(Place.class);

                                        int passengerCount = (int) (long) snapshot.child("PassengerCount").getValue();

                                        boolean isPending = true;

                                        // Make passenger entity and add it to the list
                                        PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount, isPending);
                                        listOfRequestingPassenger.add(passenger);

                                    }
                                    callback();
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
        });
    }

    public synchronized void callback() {
        requestNumber--;
        if (requestNumber <= 0) {
            if(listOfRequestingPassenger.size() == 0) {
                noOffersText.setVisibility(View.VISIBLE);
            }
            else {
                Collections.sort(listOfRequestingPassenger, new PassengerComparator());
                adapter = new D_E_RequestsRecycler(listOfRequestingPassenger, getActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
            }
            swipeContainer.setRefreshing(false);
        }
    }
}







