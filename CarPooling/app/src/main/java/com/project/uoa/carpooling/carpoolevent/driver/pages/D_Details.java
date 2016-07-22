package com.project.uoa.carpooling.carpoolevent.driver.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.dialogs.ChangeStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;


public class D_Details extends Fragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;
    private ComplexEventEntity facebookEvent;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_driver_details, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();
        facebookEvent = ((CarpoolEventActivity) getActivity()).getFacebookEvent();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // TODO: Make a helper, DisplayEventDetails(view, facebookEvent), as this can be called by Observer, Driver and Passenger
        // TODO: Reuse a details section kappa

        // EventDetails:
        // EVENT_NAME
        TextView name = (TextView) view.findViewById(R.id.event_name);
        name.setText("Name: " + facebookEvent.getName());

        // EVENT_START_TIME
        TextView startTime = (TextView) view.findViewById(R.id.event_start_datetime);
        startTime.setText("Start Time: " + facebookEvent.getPrettyStartTime());

        // EVENT_DESCRIPTION
        TextView description = (TextView) view.findViewById(R.id.event_description);
        if (facebookEvent.getDescription().equals("")) {
            description.setText("Description: No description set");
        } else {
            description.setText("Description: " + facebookEvent.getDescription());
        }

        // EVENT_LOCATION
        TextView location = (TextView) view.findViewById(R.id.event_location);
        if (facebookEvent.getLocation().toString().equals("")) {
            location.setText("Location: No location set");
        } else {
            location.setText("Location: " + facebookEvent.getLocation().toString());
        }
        // END OF EVENT_DETAILS


        // driver specific details
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


        // TODO: Might want to consider also having a helper method which sets up the buttons for the Details page
        // SetupDetailsButtons(statusButton, leaveButton, this)

        Button detailsButton = (Button) view.findViewById(R.id.change_details_button);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Change Details", "BUTTON TODO:");

            }
        });

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

                // TODO: Add an R U SURE? popup

                //remove from users
                fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
                //remove from events
                fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();

                Log.d("firebase - event", "Unsubscribed: " + eventID);

                getActivity().finish();
            }
        });
        return view;
    }
}
