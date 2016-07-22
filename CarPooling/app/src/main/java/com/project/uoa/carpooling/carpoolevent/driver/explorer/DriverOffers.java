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
import com.project.uoa.carpooling.entities.facebook.Place;

import java.util.ArrayList;


public class DriverOffers extends Fragment {
    boolean shouldExecuteOnResume;

    private View view;


    private int numberOfPublicPassengers;


    private ArrayList<PassengerEntity> listOfPublicPassengers = new ArrayList<>();


    private RecyclerView recyclerView;
    private DriverExplorerRecycler adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;

    private boolean refreshing = false;

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

        view = inflater.inflate(R.layout.carpool_driver_exp_offers, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new DriverExplorerRecycler(listOfPublicPassengers, getActivity());

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

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        return view;
    }


    public void fetchTimelineAsync() {


        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        Handler h = new Handler();
        //later to update UI
        h.post(new Runnable() {
            @Override
            public void run() {
                PopulateOffers();

            }
        });
    }


    public void PopulateOffers() {


        listOfPublicPassengers.clear();


        // CURRENT: all public passengers of event
        // TODO: check to make sure they are not on current passenger list
        // todo: add filters later (eg capacity, location....blah blah blah)


        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Status").getValue().equals("Passenger") && child.child("isPublic").getValue().equals("True")) {

                        String passengerID = child.getKey();
                        String passengerName = child.child("Name").getValue().toString();
                        String pickupName = child.child("PickupName").getValue().toString();
                        String pickupLongitude = child.child("PickupLong").getValue().toString();
                        String pickupLatitude = child.child("PickupLat").getValue().toString();

                        Place pickupLocation = new Place(pickupName, pickupLongitude, pickupLatitude);

                        String passengerCount = child.child("PassengerCount").getValue().toString();

//                        String isPending = child.child("").getValue().toString();
                        String isPending = "TODO";

                        PassengerEntity passenger = new PassengerEntity(passengerID, passengerName, pickupLocation, passengerCount, isPending);
                        listOfPublicPassengers.add(passenger);

                        PassengerEntity passenger1 = new PassengerEntity(passengerID, "2", pickupLocation, passengerCount, isPending);
                        listOfPublicPassengers.add(passenger1);

                        PassengerEntity passenger2 = new PassengerEntity(passengerID, "3", pickupLocation, passengerCount, isPending);
                        listOfPublicPassengers.add(passenger2);

                        PassengerEntity passenger3 = new PassengerEntity(passengerID, "4", pickupLocation, passengerCount, isPending);
                        listOfPublicPassengers.add(passenger3);


                    }
                }
                callback();
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });


    }

    public synchronized void callback() {
//        numberOfPublicPassengers--;
//        if (numberOfPublicPassengers == 0) {

//            Collections.sort(listOfOffers, new SimpleEventComparator()); //TODO: Sort alphabetically
//

            adapter = new DriverExplorerRecycler(listOfPublicPassengers, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
        }
//    }


}







