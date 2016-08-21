package com.project.uoa.carpooling.fragments.carpool.driver.explorer;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.fragments.carpool._entities.PassengerEntity;
import com.project.uoa.carpooling.fragments.carpool.passenger.explorer.P_E_RequestsRecycler;
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
    private TextView noOffersText;

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
        shouldExecuteOnResume = false;

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        view = inflater.inflate(R.layout.carpool_explorer_swipe_recycler, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new D_E_OffersRecycler(listOfAvailablePassengers, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        noOffersText = (TextView) view.findViewById(R.id.emptylist_text);
        noOffersText.setText("No Passengers Available!");

        DiscoverOffers();

        // Create the swipe refresher
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                populateAsync();
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

    /**
     * Discovers all the passengers the driver can offer services to.
     * Ignores Approved/Declined requests
     * Ignores passengers who already belong to a carpool (isPublic==false)
     * Ignores passengers who's count is higher than the driver's remaining capacity
     * TODO: Filters could be added in the future. These would be based around ArrivalTimes, LeaveTimes, MaxDetourTime, MaxDetourDistance.
     */
    public void DiscoverOffers() {

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listOfAvailablePassengers.clear();

                // Calculate the number of spaces left in the drivers car
                int passengerSpaceAvailable = (int)(long)snapshot.child(userID).child("Passengers").child("PassengerCapacity").getValue();
                for (DataSnapshot child : snapshot.child(userID).child("Passengers").getChildren()) {
                    if(child.getValue().equals("abandoned")) {
                        // TODO send notification + remove
                        Log.d("Notification", "TODO: PASSENGER HAS LEFT");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Passengers").child(child.getKey()).removeValue();
                    }
                    else if (!child.getKey().equals("PassengerCapacity")) {
                        int passengerCount = (int)(long) child.getValue();
                        passengerSpaceAvailable -= passengerCount;
                    }
                }

                for (DataSnapshot child : snapshot.getChildren()) {

                    // Only display Passengers who are public
                    if (child.child("Status").getValue().equals("Passenger") && (boolean) child.child("isPublic").getValue()) {

                        // Make sure that the driver can actually carry this person + additions in car
                        int passengerCount = (int)(long)child.child("PassengerCount").getValue();
                        if (passengerSpaceAvailable - passengerCount >= 0) {

                            // Fetch the passengers details
                            String passengerID = child.getKey();
                            String passengerName = child.child("Name").getValue().toString();

                            Place pickupLocation = child.child("PickupLocation").getValue(Place.class);

                            boolean isPending = false;
                            boolean hasResponse = false;

                            // Check to see if they've already been requested before
                            if (child.child("Offers").child(userID).exists()) {
                                String requestStatus = child.child("Offers").child(userID).getValue().toString();
                                if (requestStatus.equals("Pending")) {
                                    isPending = true;
                                } else if (requestStatus.equals("Decline") || requestStatus.equals("Approve")) {
                                    hasResponse = true;
                                }
                            }

                            // Do not add if the passenger has already Approved or Declined the offer.
                            if (!hasResponse) {
                                // Make passenger entity and add it to the list
                                PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount, isPending);
                                listOfAvailablePassengers.add(passenger);
                            }
                        }
                    }
                }
                PopulateOffers();
            }
        });
    }

    public synchronized void PopulateOffers() {

        if(listOfAvailablePassengers.size() == 0) {
            noOffersText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            noOffersText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            // Sort them alphabetically first
            Collections.sort(listOfAvailablePassengers, new PassengerComparator());
            adapter = new D_E_OffersRecycler(listOfAvailablePassengers, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        // Stop the refresher icon
        swipeContainer.setRefreshing(false);
    }
}







