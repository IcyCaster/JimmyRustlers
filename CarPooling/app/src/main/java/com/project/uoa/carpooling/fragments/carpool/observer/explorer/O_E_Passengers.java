package com.project.uoa.carpooling.fragments.carpool.observer.explorer;

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


public class O_E_Passengers extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private long requestNumber;
    private ArrayList<PassengerEntity> listOfPassenger = new ArrayList<>();

    private RecyclerView recyclerView;
    private O_E_PassengersRecycler adapter;
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
        adapter = new O_E_PassengersRecycler(listOfPassenger, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

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

        listOfPassenger.clear();

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Status").getValue().equals("Passenger") && child.child("isPublic").getValue().equals(true)) {

                        String passengerID = child.getKey();
                        String passengerName = child.child("Name").getValue().toString();

                        Place pickupLocation = child.child("PickupLocation").getValue(Place.class);

                        int passengerCount = (int) (long) child.child("PassengerCount").getValue();

                        // Make passenger entity and add it to the list
                        PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount);
                        listOfPassenger.add(passenger);
                    }
                }
                callback();
            }
        });
    }

    public synchronized void callback() {
        if (listOfPassenger.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            noOffersText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noOffersText.setVisibility(View.GONE);
            Collections.sort(listOfPassenger, new PassengerComparator());
            adapter = new O_E_PassengersRecycler(listOfPassenger, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        swipeContainer.setRefreshing(false);
    }
}







