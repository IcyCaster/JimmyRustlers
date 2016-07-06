package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.project.uoa.carpooling.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_Carpool_Driver.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_Carpool_Driver#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Carpool_Driver extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String EVENT_ID = "param1";

    // TODO: Rename and change types of parameters
    private Long eventId;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;
private View view;
    private LayoutInflater inflater;
    private ViewGroup container;

    private OnFragmentInteractionListener mListener;

    public Event_Carpool_Driver() {
        // Required empty public constructor
    }

    public static Event_Carpool_Driver newInstance(Long eventId) {
        Event_Carpool_Driver fragment = new Event_Carpool_Driver();

        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        this.inflater = inflater;


       view = inflater.inflate(R.layout.fragment_event_carpool_driver, container, false);

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = ((MainActivity)getActivity()).getUserId();

        // Checks DB/users/{user-id}
        fireBaseReference.child("users").child(userId).child(Long.toString(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // If it exists, everything is sweet
                if (snapshot.child("Status").getValue().equals("Observer")) {
                    Log.d("firebase - event", "You're an: Observer");
                }
                // If it doesn't, create the user in the Firebase database
                else if (snapshot.child("Status").getValue().equals("Driver")) {
                    Log.d("firebase - event", "You're a: Driver");
                } else if (snapshot.child("Status").getValue().equals("Passenger")) {
                    Log.d("firebase - event", "You're a: Passenger");
                } else {
                    Log.d("firebase - event", "Did not find: " + eventId);

                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });



        return view;
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
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
