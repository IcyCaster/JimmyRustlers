package com.project.uoa.carpooling.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.AddFacebookEventAdapter;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;
import com.project.uoa.carpooling.jsonparsers.Facebook_Id_Response;

import java.util.ArrayList;

/**
 * Created by Chester on 30/06/2016.
 */
public class EventPopup extends DialogFragment {
    private ArrayList<EventCardEntity> listOfEventCardEntities = new ArrayList<>();

    private ArrayList<String> listOfSubscribedEvents;
    private RecyclerView popUpRecyclerView;
    private AddFacebookEventAdapter adapter;
    // this method create view for your Dialog

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static EventPopup newInstance(int num) {
        EventPopup f = new EventPopup();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.popup_sub_to_events, container, false);
        popUpRecyclerView = (RecyclerView) v.findViewById(R.id.popup_fb_event_recycler);

        PopulateViewWithSubscribedEvents();

        return v;
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
                        GetEventDetails();


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

        for (String s : listOfSubscribedEvents) {

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + s,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {

                            Log.d("FB", "Event details" + response.toString());
                            listOfEventCardEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));

                            adapter = new AddFacebookEventAdapter(listOfEventCardEntities, getActivity());

                            popUpRecyclerView.setAdapter(adapter);
                            popUpRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            Log.d("FB", "Array-" + listOfEventCardEntities.toString());
                            for (EventCardEntity c : listOfEventCardEntities) {
                                Log.d("FB", "Event:" + c.toString());
                            }

                        }
                    });

            request.executeAsync();

        }
    }
}
