package com.project.uoa.carpooling.dialogs;

import android.content.Context;
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
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.FacebookEventAdapter;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;
import com.project.uoa.carpooling.jsonparsers.Facebook_Id_Response;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Chester on 30/06/2016.
 */
public class EventPopup extends DialogFragment {

    private View view;
    private RecyclerView popupRecyclerView;

    private ArrayList<EventCardEntity> listOfEventCardEntities = new ArrayList<>();
    private ArrayList<String> listOfSubscribedEvents;

    private FacebookEventAdapter adapter;
    private DatabaseReference fireBaseReference;

    private SharedPreferences sharedPreferences;
    private String userId;

    private int subbedEvents;

    /**
     * Create a new instance of Fragment
     */
    public static EventPopup newInstance() {
        EventPopup f = new EventPopup();
        return f;
    }

    public EventPopup() {

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().recreate();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.popup__new_events, container, false);
        popupRecyclerView = (RecyclerView) view.findViewById(R.id.popup_fb_event_recycler);

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("Current Facebook App-scoped ID", "");

        PopulateViewWithSubscribedEvents();

        return view;
    }

    public void PopulateViewWithSubscribedEvents() {

        long unixTime = System.currentTimeMillis() / 1000L;

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/events",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        Log.d("FB", "Event ids");
                        // This parses the events the users has subscribed to. (Currently it just parses upcoming facebook events
                        listOfSubscribedEvents = Facebook_Id_Response.parse(response.getJSONObject());

                        fireBaseReference.child("users").child(userId).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                for (DataSnapshot child : snapshot.getChildren()) {


                                    listOfSubscribedEvents.remove(child.getKey().toString());
                                    Log.d("firebase", "Event removed: " + child.getKey().toString() + " listSize: " + Integer.toString(listOfSubscribedEvents.size()));


                                }
                                if (listOfSubscribedEvents.size() == 0) {
                                    Log.d("firebase", "List Size: " + Integer.toString(listOfSubscribedEvents.size()));
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

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        parameters.putString("since", Long.toString(unixTime));
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void GetEventDetails() {

        listOfEventCardEntities = new ArrayList<EventCardEntity>();

        subbedEvents = listOfSubscribedEvents.size();
        for (int i = 0; i < listOfSubscribedEvents.size(); i++) {

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + listOfSubscribedEvents.get(i),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {

                                Log.d("FB", "Event details1" + response.toString());
                                listOfEventCardEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));


                                final String id = response.getJSONObject().getString("id");
                                Log.d("FB", id);

                                GraphRequest innerRequest = GraphRequest.newGraphPathRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        "/" + id + "/picture",
                                        new GraphRequest.Callback() {

                                            @Override
                                            public void onCompleted(GraphResponse response) {
                                                Log.d("FB", "Event details2" + response.toString());


                                                try {


                                                    String url = "";

                                                    url = response.getJSONObject().getJSONObject("data").getString("url");

                                                    Log.d("FB Picture", "url-" + url);


                                                    for (EventCardEntity e : listOfEventCardEntities) {
                                                        if (e.id == (Long.parseLong(id))) {
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

    public synchronized void callback() {
        subbedEvents--;
        if (subbedEvents == 0) {


            adapter = new FacebookEventAdapter(listOfEventCardEntities, getActivity());

            popupRecyclerView.setAdapter(adapter);
            popupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        }
    }

}



