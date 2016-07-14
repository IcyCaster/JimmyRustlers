package com.project.uoa.carpooling.fragments.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.adapters.recyclers.CurrentCarpoolEventAdapter;
import com.project.uoa.carpooling.dialogs.JoinEventDialog;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;

import org.json.JSONException;

import java.util.ArrayList;


public class CurrentCarpools extends Fragment {

    boolean shouldExecuteOnResume;
    private View view;
    private int subbedEvents;
    private ArrayList<SimpleEventEntity> listOfEventCardEntities = new ArrayList<>();
    private ArrayList<String> listOfSubscribedEvents = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private CurrentCarpoolEventAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatabaseReference fireBaseReference;
    private String userId;

    public CurrentCarpools() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        shouldExecuteOnResume = false;

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        userId = ((MainActivity) getActivity()).getUserID();

        // TODO: This will retrieve a list of all events the users is subscribed to. This list will be stored on firebase and will need to be parsed once retrieved.
        // TODO: For now, it just fetches all current events a user is subscribed to.
        PopulateViewWithSubscribedEvents();

        view = inflater.inflate(R.layout.fragment_current_car_pools, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new CurrentCarpoolEventAdapter(listOfEventCardEntities, getActivity());
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


        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        Handler h = new Handler();
        //later to update UI
        h.post(new Runnable() {
            @Override
            public void run() {
                PopulateViewWithSubscribedEvents();

            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (shouldExecuteOnResume) {
            PopulateViewWithSubscribedEvents();
        } else {
            shouldExecuteOnResume = true;
        }

    }

    public void PopulateViewWithSubscribedEvents() {


        listOfSubscribedEvents.clear();


        fireBaseReference.child("users").child(userId).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                for (DataSnapshot child : snapshot.getChildren()) {
                    listOfSubscribedEvents.add(child.getKey().toString());
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


        listOfEventCardEntities.clear();

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

                                    listOfEventCardEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));
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
                                                            if (e.eventID.equals(id)) {
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
        subbedEvents--;
        if (subbedEvents == 0) {
            swipeContainer.setRefreshing(false);
            adapter = new CurrentCarpoolEventAdapter(listOfEventCardEntities, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}







