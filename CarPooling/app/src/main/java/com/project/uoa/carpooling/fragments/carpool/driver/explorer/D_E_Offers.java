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


public class D_E_Offers extends Fragment {

    private View view;
    private boolean shouldExecuteOnResume;

    private ArrayList<PassengerEntity> listOfAvailablePassengers = new ArrayList<>();

    private RecyclerView recyclerView;
    private D_E_OffersRecycler adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;

    private String userID;
    private String eventID;

    /**
     * This method executes only if the users re-opens the application.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            DiscoverOffers();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Firebase Reference
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // UserID / EventID
        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        DiscoverOffers();
        shouldExecuteOnResume = false;

        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new D_E_OffersRecycler(listOfAvailablePassengers, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        // Create the swipe refresher
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
//                populateAsync(); // Not sure why I'm populating asynchronously... Will keep this here just in case.
                DiscoverOffers();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    // Kept but not used
    public void populateAsync() {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                DiscoverOffers();

            }
        });
    }

    public void DiscoverOffers() {

        listOfAvailablePassengers.clear();
        // CURRENT: all public passengers of event
        // TODO: check to make sure they are not on current passenger list
        // todo: add filters later (eg capacity, location....blah blah blah)

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Status").getValue().equals("Passenger") && child.child("isPublic").getValue().equals("True")) {

                        String passengerID = child.getKey();
                        String passengerName = child.child("Name").getValue().toString();
//                        String pickupName = child.child("PickupName").getValue().toString();
                        String pickupLongitude = child.child("PickupLong").getValue().toString();
                        String pickupLatitude = child.child("PickupLat").getValue().toString();

                        Place pickupLocation = new Place("", Double.parseDouble(pickupLongitude), Double.parseDouble(pickupLatitude));

                        String passengerCount = child.child("PassengerCount").getValue().toString();

                        String isPending = "False";
                        if (child.child("Offers").exists()) {
                            for (DataSnapshot UID : child.child("Offers").getChildren()) {
                                if (UID.getKey().equals(userID)) {
                                    if (UID.getValue().equals("Pending")) {
                                        isPending = "True";
                                    }
                                    //Might consider adding something for "Decline" here? Not sure?
                                }

                            }
                        }

                        // Make passenger entity and add it to the list
                        PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount, isPending);
                        listOfAvailablePassengers.add(passenger);
                    }
                }
                PopulateOffers();
            }
        });
    }

    public synchronized void PopulateOffers() {
        // Sort them alphabetically first
        Collections.sort(listOfAvailablePassengers, new PassengerComparator());
        adapter = new D_E_OffersRecycler(listOfAvailablePassengers, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        // Stop the refresher icon
        swipeContainer.setRefreshing(false);
    }
}







