package com.project.uoa.carpooling.fragments.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_SimpleEvent_Parser;
import com.project.uoa.carpooling.adapters.recyclers.CurrentCarpoolEventAdapter;
import com.project.uoa.carpooling.dialogs.JoinEventDialog;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.helpers.comparators.SimpleEventComparator;
import com.project.uoa.carpooling.helpers.firebase.FirebaseChildEventListener;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CurrentCarpools extends Fragment {

    // boolean to populate the list onResume()
    boolean shouldExecuteOnResume;

    // Fields for the view components
    private View view;
    private TextView emptyListText;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private CurrentCarpoolEventAdapter adapter;

    // Fields related to the list of subscribed events
    private int numberOfSubscribedEvents;
    private ArrayList<String> listOfSubscribedEvents = new ArrayList<>();
    private ArrayList<SimpleEventEntity> listOfEventCardEntities = new ArrayList<>();

    private DatabaseReference fireBaseReference;
    private String userID;

    // HashMap for registering the isDriving notifications/broadcasts
    private HashMap<String, ChildEventListener> isDrivingRefMap = new HashMap<>();

    // Required empty public constructor
    public CurrentCarpools() {
    }

    @Override
    public void onResume() {
        super.onResume();
        // This allows the list to repopulate when the user resumes
        if (shouldExecuteOnResume) {
            Log.d("resuming", "CurrentCarpools");
            PopulateViewWithSubscribedEvents();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        shouldExecuteOnResume = false;

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        userID = ((MainActivity) getActivity()).getUserID();

        PopulateViewWithSubscribedEvents();

        view = inflater.inflate(R.layout.fragment_current_car_pools, container, false);

        emptyListText = (TextView) view.findViewById(R.id.emptylist_text);
        emptyListText.setText("No Carpools Available! \n Join one below!");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new CurrentCarpoolEventAdapter(listOfEventCardEntities, getActivity());
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

        Button addButton = (Button) view.findViewById(R.id.join_carpool_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                JoinEventDialog newFragment = new JoinEventDialog();
                newFragment.show(ft, "dialog");
            }


        });
        return view;
    }

    public void fetchTimelineAsync() {
        Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                Log.d("PopulatingAsync", "Subscribed Events");
                PopulateViewWithSubscribedEvents();
            }
        });
    }


    // Gets all events the user is subscribed to from firebase
    public void PopulateViewWithSubscribedEvents() {
        listOfSubscribedEvents.clear();
        fireBaseReference.child("users").child(userID).child("events").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    listOfSubscribedEvents.add(child.getKey().toString());
                }
                GetEventDetails();
            }


        });
    }


    public void GetEventDetails() {

        listOfEventCardEntities.clear();

        if (listOfSubscribedEvents.size() == 0) {

            Log.d("SubEventCount", "0");
            // Display a message telling the user that they should subscribe to events below.
            emptyListText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            swipeContainer.setRefreshing(false);
        } else {

            Log.d("SubEventCount", Integer.toString(listOfSubscribedEvents.size()));
            emptyListText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            numberOfSubscribedEvents = listOfSubscribedEvents.size();
            for (int i = 0; i < listOfSubscribedEvents.size(); i++) {

                fireBaseReference.child("events").child(listOfSubscribedEvents.get(i)).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String eventID = dataSnapshot.getKey().toString();

                        // Check if they are a passenger for this event
                        if (dataSnapshot.child("users").child(userID).child("Status").getValue().equals("Passenger")) {

                            final String driverID = dataSnapshot.child("users").child(userID).child("Driver").getValue().toString();

                            // Check if they have a specified driver
                            if (!driverID.equals("null")) {

                                final String driverName = dataSnapshot.child("users").child(driverID).child("Name").getValue().toString();

                                // Attach valueListener
                                fireBaseReference.child("events").child(dataSnapshot.getKey()).child("users").child(driverID).child("isDriving").addValueEventListener(new FirebaseValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        DatabaseReference currentLocationRef = fireBaseReference.child("events").child(eventID).child("users").child(driverID);

                                        // Detect that the driver is driving; trigger notification
                                        if ((boolean) dataSnapshot.getValue()) {
                                            if (isAdded()) {
                                                Log.d("Notification", driverName + " is driving!");
                                                showNotification(driverName);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + listOfSubscribedEvents.get(i),
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {

                                    listOfEventCardEntities.add(Facebook_SimpleEvent_Parser.parse(response.getJSONObject()));
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
                                                        for (SimpleEventEntity e : listOfEventCardEntities) {
                                                            if (e.getEventID().equals(id)) {
                                                                listOfEventCardEntities.get(listOfEventCardEntities.indexOf(e)).setImage(url);
                                                            }
                                                        }

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
        numberOfSubscribedEvents--;
        if (numberOfSubscribedEvents <= 0) {

            Collections.sort(listOfEventCardEntities, new SimpleEventComparator());
            adapter = new CurrentCarpoolEventAdapter(listOfEventCardEntities, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
        }
    }

    private void showNotification(String driverName) {

        // Reference: http://stackoverflow.com/questions/13902115/how-to-create-a-notification-with-notificationcompat-builder
        int requestID = (int) System.currentTimeMillis();

        int mID = 100;
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), requestID, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.icon_grey_driving)
                        .setContentTitle(driverName + " is Driving!")
                        .setContentText("Touch to see how far away he is from you!")
                        .setContentIntent(pendingIntent); //Required on Gingerbread and below

        //TODO: Add click to launch carpool event on map page.

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mID, mBuilder.build());
    }

    public void broadcastIntent(double longitude, double latitude, String eventID, String driverID) {
        Intent intent = new Intent();
        intent.setAction(eventID + "-" + driverID);
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longitude", longitude);
        getActivity().sendBroadcast(intent);
    }
}







