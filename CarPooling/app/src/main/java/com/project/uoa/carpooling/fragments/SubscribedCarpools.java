package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.SubscribedFacebookEventAdapter;
import com.project.uoa.carpooling.dialogs.EventPopup;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribedCarpools.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribedCarpools#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribedCarpools extends Fragment {

    // These ARG PARAMS are used if we have arguments that must be provided to the fragment.

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private int subbedEvents;

    // This is the list of cards which will be displayed on the recyclerView
    private ArrayList<EventCardEntity> listOfEventCardEntities = new ArrayList<>();

    private ArrayList<String> listOfSubscribedEvents;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private SubscribedFacebookEventAdapter adapter;

    private SwipeRefreshLayout swipeContainer;

    //Firebase things
    private DatabaseReference fireBaseReference;

    private SharedPreferences sharedPreferences;
    private String userId;

    public SubscribedCarpools() {
        // Required empty public constructor
    }

    /**
     * This factory method will create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscribedCarpools.
     */
    // TODO: Rename and change types and number of parameters
    public static SubscribedCarpools newInstance(String param1, String param2) {
        SubscribedCarpools fragment = new SubscribedCarpools();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("Current Facebook App-scoped ID", "");

        listOfSubscribedEvents = new ArrayList<>();

        // TODO: This will retrieve a list of all events the users is subscribed to. This list will be stored on firebase and will need to be parsed once retrieved.
        // TODO: For now, it just fetches all current events a user is subscribed to.
        PopulateViewWithSubscribedEvents();

        view = inflater.inflate(R.layout.fragment_car_pools, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                fetchTimelineAsync();
            }
        });

//        swipeContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeContainer.setRefreshing(true);
//            }
//        });

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
                DialogFragment newFragment = EventPopup.newInstance();
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

    public void PopulateViewWithSubscribedEvents() {


        listOfSubscribedEvents.clear();

        Log.d("firebase - currentId", userId);
        fireBaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.d("firebase - listsnapshot", snapshot.toString());
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (!child.getKey().toString().equals("Name")) {
                        Log.d("firebase - list event", child.toString());
                        listOfSubscribedEvents.add(child.getKey().toString());
                    } else {
                        Log.d("firebase - list name", child.toString());
                    }
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


        listOfEventCardEntities = new ArrayList<EventCardEntity>();

        if (listOfSubscribedEvents.size() == 0) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "NO POOLS CURRENTLY JOINED \n Join one below!",
                    Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();

        } else {

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
                                                    Log.d("FB", "Event details" + response.toString());


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
    }

    public synchronized void callback() {
        subbedEvents--;
        if (subbedEvents == 0) {
            swipeContainer.setRefreshing(false);
            adapter = new SubscribedFacebookEventAdapter(listOfEventCardEntities, getActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            Log.d("FB", "Array-" + listOfEventCardEntities.toString());
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







