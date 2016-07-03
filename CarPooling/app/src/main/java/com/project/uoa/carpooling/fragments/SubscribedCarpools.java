package com.project.uoa.carpooling.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.EventToCardAdapter;
import com.project.uoa.carpooling.dialogs.EventPopup;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.firebaseModels.DBItemModel;
import com.project.uoa.carpooling.jsonparsers.Facebook_Event_Response;
import com.project.uoa.carpooling.jsonparsers.Facebook_Id_Response;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


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

    // This is the list of cards which will be displayed on the recyclerView
    private ArrayList<EventCardEntity> listOfEventCardEntities = new ArrayList<>();

    private ArrayList<String> listOfSubscribedEvents;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private EventToCardAdapter adapter;

    private SwipeRefreshLayout swipeContainer;

    //Firebase things
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;

    private FirebaseRecyclerAdapter<DBItemModel, ItemViewHolder>
            mFirebaseAdapter;


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

        // TODO: This will retrieve a list of all events the users is subscribed to. This list will be stored on firebase and will need to be parsed once retrieved.
        // TODO: For now, it just fetches all current events a user is subscribed to.
        PopulateViewWithSubscribedEvents();

        view = inflater.inflate(R.layout.fragment_car_pools, container, false);

//        recyclerView = (RecyclerView) view.findViewById(R.id.rv);


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
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
                DialogFragment newFragment = EventPopup.newInstance(0);
                newFragment.show(ft, "dialog");
            }


        });


        //-------------------


        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.setValue("Hello, World!");

        // Set up Adapter for RecyclerView
        // Note that the adapter requires the new DBItemModel and ItemViewHolder classes.
        mFirebaseAdapter = new FirebaseRecyclerAdapter<DBItemModel,
                ItemViewHolder>(
                DBItemModel.class,
                R.layout.item_message,
                ItemViewHolder.class,
                mFirebaseDatabaseReference.child("messages")) {

            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder,
                                              DBItemModel DBItemModel, int position) {
                viewHolder.messageTextView.setText(DBItemModel.getText());
                viewHolder.userTextView.setText(DBItemModel.getName());
            }
        };


        // Set up RecyclerView with LayoutManager and Adapter
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        return view;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView userTextView;

        public ItemViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            userTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        }
    }



    //-------------------------------------------------------
    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        Handler h = new Handler();
        //later to update UI
        h.post(new Runnable() {
            @Override
            public void run() {
                PopulateViewWithSubscribedEvents();
                swipeContainer.setRefreshing(false);
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

    public void PopulateViewWithSubscribedEvents() {

        // Old method which got Facebook events. This is now getting subscribed FireBase Events.
//        long unixTime = System.currentTimeMillis() / 1000L;
//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me/events",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//
//                        Log.d("FB", "Event ids");
//                        // This parses the events the users has subscribed to. (Currently it just parses upcoming facebook events
//                        listOfSubscribedEvents = Facebook_Id_Response.parse(response.getJSONObject());
//                        GetEventDetails();
//
//
//                    }
//                });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id");
//        parameters.putString("since", Long.toString(unixTime));
//        request.setParameters(parameters);
//        request.executeAsync();


//        listOfSubscribedEvents = Facebook_Id_Response.parse(response.getJSONObject());
//        GetEventDetails();


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


                            //TODO: REMOVE, only added two more times to test RecyclerView
                            listOfEventCardEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));
                            listOfEventCardEntities.add(Facebook_Event_Response.parse(response.getJSONObject()));

                            adapter = new EventToCardAdapter(listOfEventCardEntities, getActivity());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            Log.d("FB", "Array-" + listOfEventCardEntities.toString());
                            for (EventCardEntity c : listOfEventCardEntities) {
                                Log.d("FB", "Event:" + c.toString());
                            }

                        }
                    });

            request.executeAsync();

        }
    }


    // OLD METHOD TO TEST OUT THE RECYCLER
//    public List<EventCardEntity> fill_with_data() {
//
//        List<EventCardEntity> data = new ArrayList<>();
//
//        data.add(new EventCardEntity(1, R.drawable.test_bbq, "TEST ONE", "1-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(2, R.drawable.test_church, "TEST TWO", "2-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(3, R.drawable.test_work, "TEST THREE", "3-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(4, R.drawable.test_bbq, "TEST FOUR", "4-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(5, R.drawable.test_church, "TEST FIVE", "5-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(6, R.drawable.test_work, "TEST SIX", "6-Sept-16 8:00pm"));
//        data.add(new EventCardEntity(7, R.drawable.test, "TEST SEVEN", "7-Sept-16 11:00pm"));
//
//        return data;
//    }

}





