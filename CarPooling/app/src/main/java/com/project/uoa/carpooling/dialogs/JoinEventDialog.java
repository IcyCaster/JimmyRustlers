package com.project.uoa.carpooling.dialogs;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.adapters.recyclers.ExploreCarpoolEventAdapter;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_SimpleEvent_Parser;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_ID_Parser;
import com.project.uoa.carpooling.helpers.SimpleEventComparator;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Chester on 30/06/2016.
 */
public class JoinEventDialog extends DialogFragment {

    private View view;
    private RecyclerView recyclerView;

    private ArrayList<SimpleEventEntity> listOfEventCardEntities = new ArrayList<>();
    private ArrayList<String> listOfSubscribedEvents = new ArrayList<>();

    private ExploreCarpoolEventAdapter adapter;
    private DatabaseReference fireBaseReference;

    private SharedPreferences sharedPreferences;
    private String userId;

    private int subbedEvents;

    public JoinEventDialog() {

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().getSupportFragmentManager().findFragmentById(R.id.contentFragment).onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.dialog__explore_carpool_events, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.popup_fb_event_recycler);
        adapter = new ExploreCarpoolEventAdapter(listOfEventCardEntities, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        userId = ((MainActivity) getActivity()).getUserID();

        PopulateViewWithSubscribedEvents();

        return view;
    }

    public void PopulateViewWithSubscribedEvents() {

        // Gets the current time
        long unixTime = System.currentTimeMillis() / 1000L;

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/events",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {


                        listOfSubscribedEvents = Facebook_ID_Parser.parse(response.getJSONObject());

                        fireBaseReference.child("users").child(userId).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                // Remove events off the list if they have already been subscribed to
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    listOfSubscribedEvents.remove(child.getKey().toString());
                                }
                                if (listOfSubscribedEvents.size() == 0) {
                                    Log.d("firebase", "No event left to subscribe to");
                                    Toast.makeText(getActivity().getApplicationContext(), "No Facebook event left to join :(",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    GetEventDetails();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                Log.e("firebase - error", firebaseError.getMessage());
                            }
                        });
                    }
                });

        // Facebook parameters for getting events which haven't expired.
        // TODO: Events should not be expired if less than 24h past their start time
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        parameters.putString("since", Long.toString(unixTime));
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void GetEventDetails() {

        listOfEventCardEntities.clear();

        subbedEvents = listOfSubscribedEvents.size();

        for (int i = 0; i < listOfSubscribedEvents.size(); i++) {

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

                                // ----------------------- BATCH EXAMPLE
                                ArrayList<GraphRequest> listOfReq = new ArrayList<GraphRequest>();
                                listOfReq.add(innerRequest);
                                GraphRequestBatch batch = new GraphRequestBatch(listOfReq);
                                batch.addCallback(new GraphRequestBatch.Callback() {

                                    @Override
                                    public void onBatchCompleted(GraphRequestBatch batch) {
                                        Log.d("Facebook", "BATCH");
                                    }

                                });
                                // ----------------------- BATCH EXAMPLE

                                // Facebook parameters for getting large images.
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

    // Callback ensures that the recycler doesn't update until the end
    public synchronized void callback() {
        subbedEvents--;
        if (subbedEvents == 0) {

            Collections.sort(listOfEventCardEntities, new SimpleEventComparator());

            adapter = new ExploreCarpoolEventAdapter(listOfEventCardEntities, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
    }
}



