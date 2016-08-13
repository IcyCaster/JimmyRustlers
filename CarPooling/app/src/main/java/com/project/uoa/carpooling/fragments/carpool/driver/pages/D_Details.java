package com.project.uoa.carpooling.fragments.carpool.driver.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool.DetailsFragment;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;


public class D_Details extends DetailsFragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_details_driver, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

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


        return view;
    }
}
