package com.project.uoa.carpooling.fragments.carpool.passenger.explorer;

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
import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;
import com.project.uoa.carpooling.helpers.comparators.DriverComparator;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class P_E_Requests extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private ArrayList<DriverEntity> listOfPotentialDrivers = new ArrayList<>();

    private RecyclerView recyclerView;
    private P_E_RequestsRecycler adapter;
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
        adapter = new P_E_RequestsRecycler(listOfPotentialDrivers, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        noOffersText = (TextView) view.findViewById(R.id.emptylist_text);
        noOffersText.setText("No Drivers Available!");

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

        listOfPotentialDrivers.clear();

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Check to make sure they don't have a driver
                if (snapshot.child(userID).child("Driver").getValue().toString().equals("null")) {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.child("Status").getValue().equals("Driver") && child.child("isPublic").getValue().equals(true)) {

                            String driverID = child.getKey();
                            String driverName = child.child("Name").getValue().toString();

                            // Location CURRENTLY NOT USED in driverEntity constructor
                            // Place startLocation = child.child("StartLocation").getValue(Place.class);

                            int carCapacity = (int) (long) child.child("Passengers").child("PassengerCapacity").getValue();
                            //TODO: Calculate total space and compare it with number of passengers

                            boolean isPending = false;
                            if (child.child("Requests").exists()) {
                                for (DataSnapshot UID : child.child("Requests").getChildren()) {
                                    if (UID.getKey().equals(userID)) {
                                        if (UID.getValue().equals("Pending") || UID.getValue().equals("Decline")) {
                                            isPending = true;
                                        }
                                    }

                                }
                            }

                            // Make driver entity and add it to the list
                            DriverEntity driver = new DriverEntity(driverID, driverName, isPending, carCapacity);
                            listOfPotentialDrivers.add(driver);
                        }
                    }
                }
                callback();
            }

        });
    }

    public synchronized void callback() {
        if (listOfPotentialDrivers.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            noOffersText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noOffersText.setVisibility(View.GONE);
            Collections.sort(listOfPotentialDrivers, new DriverComparator());
            adapter = new P_E_RequestsRecycler(listOfPotentialDrivers, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        swipeContainer.setRefreshing(false);
    }
}







