package com.project.uoa.carpooling.fragments.carpool.observer.explorer;

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
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.helpers.comparators.DriverComparator;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class O_E_Drivers extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private ArrayList<DriverEntity> listOfDrivers = new ArrayList<>();

    private RecyclerView recyclerView;
    private O_E_DriversRecycler adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;

    private String userID;
    private String eventID;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            PopulateDrivers();
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

        PopulateDrivers();

        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new O_E_DriversRecycler(listOfDrivers, getActivity());

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
                PopulateDrivers();

            }
        });
    }

    public void PopulateDrivers() {

        listOfDrivers.clear();

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Status").getValue().equals("Driver") && child.child("isPublic").getValue().equals(true)) {

                        String driverID = child.getKey();
                        String driverName = child.child("Name").getValue().toString();

                        // Location currently not used for driverEntity
                        String startLongitude = child.child("StartLong").getValue().toString();
                        String startLatitude = child.child("StartLat").getValue().toString();
                        Place startLocation = new Place("", Double.parseDouble(startLongitude), Double.parseDouble(startLatitude));

                        String carCapacity = child.child("Passengers").child("PassengerCapacity").getValue().toString();
                        //TODO: Calculate total space and compare it with number of passengers

                        // Make driver entity and add it to the list
                        DriverEntity driver = new DriverEntity(driverID, driverName, carCapacity);
                        listOfDrivers.add(driver);
                    }
                }
                callback();
            }

        });
    }

    public synchronized void callback() {
        Collections.sort(listOfDrivers, new DriverComparator());
        adapter = new O_E_DriversRecycler(listOfDrivers, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        swipeContainer.setRefreshing(false);
    }
}







