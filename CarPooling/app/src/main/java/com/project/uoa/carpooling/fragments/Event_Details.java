package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_Details.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_Details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Details extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EVENT_ID = "param1";

    // TODO: Rename and change types of parameters
    private Long eventId;

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;
    private String eventStatus;

    private View view;

    private OnFragmentInteractionListener mListener;

    public Event_Details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Event_Details.
     */
    // TODO: Rename and change types and number of parameters
    public static Event_Details newInstance(Long eventId) {
        Event_Details fragment = new Event_Details();
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

        view = inflater.inflate(R.layout.fragment_event_driver_details, container, false);

        eventStatus = ((CarpoolEventActivity)getActivity()).getEventStatus();
        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        if(eventStatus.equals("Observer")) {
            view = inflater.inflate(R.layout.fragment_event_observer_details, container, false);

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + eventID,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here













                        }
                    });

            request.executeAsync();

















        } else if(eventStatus.equals("Driver")) {
            view = inflater.inflate(R.layout.fragment_event_driver_details, container, false);

        } else if(eventStatus.equals("Passenger")) {
            view = inflater.inflate(R.layout.fragment_event_passenger_details, container, false);

        }



        Button leaveButton = (Button) view.findViewById(R.id.leave_carpool_button);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("test", userID + "   " + eventID);

                fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            //remove from users
                            fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
                            //remove from events
                            fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();
                            Log.d("firebase - event", "Unsubscribed: " + eventId);
                        }
                        // If it doesn't, create the user in the Firebase database
                        else {
                            Log.d("firebase - event", "Can't find: " + eventId);
                        }

                        getActivity().finish();

                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("firebase - error", firebaseError.getMessage());
                    }
                });
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
