package com.project.uoa.carpooling.fragments.carpool;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.project.uoa.carpooling.dialogs.ChangeStatusDialog;

import org.json.JSONException;


public class Event_Details extends Fragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;
    private String eventStatus;

    private View view;

    private OnFragmentInteractionListener mListener;

    public Event_Details() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_event_details_driver, container, false);

        eventStatus = ((CarpoolEventActivity) getActivity()).getEventStatus();
        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + eventID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        TextView name = (TextView) view.findViewById(R.id.event_name);
                        TextView description = (TextView) view.findViewById(R.id.event_description);
                        TextView starttime = (TextView) view.findViewById(R.id.event_start_datetime);
                        TextView location = (TextView) view.findViewById(R.id.event_location);

                        try {
                            name.setText("Name: " + response.getJSONObject().getString("name"));
                            starttime.setText("Start Time: " + response.getJSONObject().getString("start_time"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            description.setText("Description: " + response.getJSONObject().getString("description"));
                        } catch (JSONException e) {
                            description.setText("Description: " + "NO DESCRIPTION");
                        }
                        try {
                            location.setText("Location: " + response.getJSONObject().getJSONObject("place").getString("name"));
                        } catch (JSONException e) {
                            location.setText("Location: " + "NO LOCATION");
                        }

                    }
                });

        request.executeAsync();


        if (eventStatus.equals("Observer")) {
            view = inflater.inflate(R.layout.fragment_event_details_observer, container, false);


        } else if (eventStatus.equals("Driver")) {
            view = inflater.inflate(R.layout.fragment_event_details_driver, container, false);

            fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    TextView passengerText = (TextView) view.findViewById(R.id.passenger_names);


                    String passengers = "";
                    for (DataSnapshot child : snapshot.child("Passengers").getChildren()) {
                        if (!child.getKey().equals("PassengerCapacity")) {
                            passengers = passengers + child.getKey() + "(" + child.getValue() + "); ";
                        }
                    }
                    if (passengers.equals("")) {
                        passengerText.setText("Passengers: No current passengers");
                    } else {
                        passengerText.setText("Passengers: " + passengers);
                    }


                    // Starting Route Time AND Estimated Arrival Time will need to be calculated based on start destination, passengers destination and the event's start time.

                    TextView countText = (TextView) view.findViewById(R.id.information_count);
                    countText.setText("Passenger Capacity: " + snapshot.child("Passengers").child("PassengerCapacity").getValue().toString());

                    TextView locationText = (TextView) view.findViewById(R.id.information_location);
                    locationText.setText("Leave location: " + snapshot.child("StartLat").getValue().toString() + "   " + snapshot.child("StartLong").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("firebase - error", firebaseError.getMessage());
                }
            });


        } else if (eventStatus.equals("Passenger")) {
            view = inflater.inflate(R.layout.fragment_event_details_passenger, container, false);


            fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    TextView driverText = (TextView) view.findViewById(R.id.driver_name);

                    String driver = snapshot.child("Driver").getValue().toString();

                    if (driver.equals("null")) {
                        driverText.setText("Driver: No driver yet");
                    } else {
                        driverText.setText("Driver: " + driver);
                    }


                    // Starting Route Time AND Estimated Arrival Time will need to be calculated based on start destination, passengers destination and the event's start time.

                    TextView countText = (TextView) view.findViewById(R.id.information_count);
                    countText.setText("Passenger Total: " + snapshot.child("PassengerCount").getValue().toString());

                    TextView locationText = (TextView) view.findViewById(R.id.information_location);
                    locationText.setText("Leave location: " + snapshot.child("PickupLat").getValue().toString() + "   " + snapshot.child("PickupLong").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("firebase - error", firebaseError.getMessage());
                }
            });

        }


        Button statusButton = (Button) view.findViewById(R.id.change_status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("status_dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                ChangeStatusDialog newFragment = new ChangeStatusDialog();
                newFragment.show(ft, "status_dialog");

            }
        });

        Button leaveButton = (Button) view.findViewById(R.id.leave_carpool_button);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            //remove from users
                            fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
                            //remove from events
                            fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();
                            Log.d("firebase - event", "Unsubscribed: " + eventID);
                        }
                        // If it doesn't, create the user in the Firebase database
                        else {
                            Log.d("firebase - event", "Can't find: " + eventID);
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
