package com.project.uoa.carpooling.carpoolevent.driver.explorer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_SimpleEvent_Parser;
import com.project.uoa.carpooling.adapters.recyclers.CurrentCarpoolEventAdapter;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.entities.firebase.PassengersEntity;
import com.project.uoa.carpooling.helpers.SimpleEventComparator;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;


public class DriverOffers extends Fragment {
    boolean shouldExecuteOnResume;

    private View view;




    private int subbedEvents;




    private ArrayList<PassengersEntity> listOfOffers = new ArrayList<>();


    private ArrayList<String> listOfSubscribedEvents = new ArrayList<>();


    private RecyclerView recyclerView;
    private DriverExplorerRecycler adapter;
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

        view = inflater.inflate(R.layout.carpool_driver_exp_offers, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new DriverExplorerRecycler(listOfOffers, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
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


        listOfSubscribedEvents.clear();



        // CURRENT: all passengers of event
        // NEXT: check each passenger to see if they are public
        // TODO: check to make sure they are not on current passenger list
        // todo: add filters later (eg capacity, location....blah blah blah)



        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if(child.child("Status").getValue().equals("Passenger") && child.child("isPublic").getValue().equals("True")) {
                        Log.d("T","T");
                    }



//                    listOfSubscribedEvents.add(child.getKey().toString());
                }

                GetEventDetails();

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });


    }


    public void GetEventDetails() {


        listOfOffers.clear();

        if (listOfSubscribedEvents.size() == 0) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "NO POOLS CURRENTLY JOINED \n Join one below!",
                    Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();

            recyclerView.setVisibility(View.GONE);

        } else {

            recyclerView.setVisibility(View.VISIBLE);

            subbedEvents = listOfSubscribedEvents.size();
            for (int i = 0; i < listOfSubscribedEvents.size(); i++) {


                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + listOfSubscribedEvents.get(i),
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {


                                    final String id = response.getJSONObject().getString("id");

                                    GraphRequest innerRequest = GraphRequest.newGraphPathRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/" + id + "/picture",
                                            new GraphRequest.Callback() {

                                                @Override
                                                public void onCompleted(GraphResponse response) {


                                                    try {


                                                        String url = "";

                                                        url = response.getJSONObject().getJSONObject("data").getString("url");


//                                                        for (SimpleEventEntity e : listOfOffers) {
//                                                            if (e.getEventID().equals(id)) {
//                                                                listOfOffers.get(listOfOffers.indexOf(e)).setImage(url);
//                                                            }
//                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    callback();
                                                }
                                            });


                                    Bundle parameters = new Bundle();
                                    parameters.putString("type", "large");
                                    parameters.putBoolean("redirect", false);
                                    innerRequest.setParameters(parameters);
                                    innerRequest.executeAsync();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                request.executeAsync();

            }
        }
    }

    public synchronized void callback() {
        subbedEvents--;
        if (subbedEvents == 0) {

//            Collections.sort(listOfOffers, new SimpleEventComparator());
//
//            swipeContainer.setRefreshing(false);
//            adapter = new CurrentCarpoolEventAdapter(listOfOffers, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        }
    }


}







