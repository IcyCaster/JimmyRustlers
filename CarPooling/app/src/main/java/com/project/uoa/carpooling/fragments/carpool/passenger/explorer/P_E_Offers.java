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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;
import com.project.uoa.carpooling.helpers.comparators.DriverComparator;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

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
    private TextView noOffersText;

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



        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);

        noOffersText = (TextView) view.findViewById(R.id.emptylist_text);
        noOffersText.setText("No Drivers Available!");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new P_E_OffersRecycler(listOfOffersFromDrivers, getActivity());


        PopulateOffers();

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
        noOffersText.setVisibility(View.GONE);

        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.child("Offers").exists()) {

                    requestNumber = snapshot.child("Offers").getChildrenCount();

                    for (DataSnapshot requests : snapshot.child("Offers").getChildren()) {

                        if (requests.getValue().equals("Pending")) {

                            fireBaseReference.child("events").child(eventID).child("users").child(requests.getKey()).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    String driverID = snapshot.getKey();
                                    String driverName = snapshot.child("Name").getValue().toString();


                                    int carCapacity = (int)(long)snapshot.child("Passengers").child("PassengerCapacity").getValue();
                                    //TODO: Calculate total space and compare it with number of passengers

                                    boolean isPending = true;

                                    // Make driver entity and add it to the list
                                    DriverEntity driver = new DriverEntity(driverID, driverName, isPending, carCapacity);
                                    listOfOffersFromDrivers.add(driver);

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

            if(listOfOffersFromDrivers.size() == 0) {
                noOffersText.setVisibility(View.VISIBLE);
            }
            else {

                Collections.sort(listOfOffersFromDrivers, new DriverComparator());
                adapter = new P_E_OffersRecycler(listOfOffersFromDrivers, getActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
            }
            swipeContainer.setRefreshing(false);
        }
    }
}







