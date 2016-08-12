package com.project.uoa.carpooling.fragments.carpool.driver.pages;

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
import com.project.uoa.carpooling.fragments.carpool.DetailsFragment;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;


public class D_Details extends DetailsFragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;
    private ComplexEventEntity facebookEvent;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_details_driver, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();
        facebookEvent = ((CarpoolEventActivity) getActivity()).getFacebookEvent();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        super.addEventDetails(view);

        // driver specific details
        fireBaseReference.child("events").child(eventID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DataSnapshot userSnapshot = snapshot.child("users").child(userID);

                TextView passengerText = (TextView) view.findViewById(R.id.passengers_text);

                String passengers = "";
                int passengerNumber = 0;
                for (DataSnapshot child : userSnapshot.child("Passengers").getChildren()) {
                    if (!child.getKey().equals("PassengerCapacity")) {
                        String passengerName = snapshot.child("users").child(child.getKey()).child("Name").getValue().toString();
                        passengers = passengers + passengerName + "(" + child.getValue() + "); ";
                        passengerNumber += (int)(long)child.getValue();
                    }
                }
                if (passengerNumber == 0) {
                    passengerText.setText("No Passengers");
                } else {
                    passengerText.setText(passengers);
                }


                // Starting Route Time AND Estimated Arrival Time will need to be calculated based on start destination, passengers destination and the event's start time.

                TextView countText = (TextView) view.findViewById(R.id.passenger_capacity_text);
                if((int)(long)userSnapshot.child("Passengers").child("PassengerCapacity").getValue() == passengerNumber) {
                    countText.setText("Capacity: FULL");
                }else {
                    countText.setText("Capacity: (" + passengerNumber + "/" + userSnapshot.child("Passengers").child("PassengerCapacity").getValue().toString() + ")");
                }


                TextView locationText = (TextView) view.findViewById(R.id.starting_location_placename);
                locationText.setText(userSnapshot.child("StartLocation").child("latitude").getValue().toString() + "   " + userSnapshot.child("StartLocation").child("longitude").getValue().toString());
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
