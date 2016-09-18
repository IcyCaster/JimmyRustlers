package com.project.uoa.carpooling.fragments.carpool.passenger.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool.DetailsFragment;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;


public class P_Details extends DetailsFragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_details_passenger, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();


        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        super.addEventDetails(view);

        // passenger specific details
        fireBaseReference.child("events").child(eventID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DataSnapshot userSnapshot = snapshot.child("users").child(userID);

                TextView driverText = (TextView) view.findViewById(R.id.driver_name_text);

                RelativeLayout routeDetails = (RelativeLayout) view.findViewById(R.id.route_details_container);
                View routeDetailsBreak = view.findViewById(R.id.route_details_break);

                if (userSnapshot.child("Driver").getValue().equals("abandoned")) {
                    // TODO send notification + set abandoned to null
                    Log.d("Notification", "TODO: DRIVER HAS LEFT");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Driver").setValue("null");
                }

                if (userSnapshot.child("Driver").getValue().equals("null") || userSnapshot.child("Driver").getValue().equals("abandoned")) {
                    driverText.setText("No Driver Yet!");
                    routeDetails.setVisibility(View.GONE);
                    routeDetailsBreak.setVisibility(View.GONE);

                } else {
                    String driverID = userSnapshot.child("Driver").getValue().toString();
                    String driverName = snapshot.child("users").child(driverID).child("Name").getValue().toString();
                    driverText.setText(driverName);
                    //TODO: Set route details
                }

                TextView countText = (TextView) view.findViewById(R.id.passenger_number_text);
                countText.setText(userSnapshot.child("PassengerCount").getValue().toString());

                TextView locationText = (TextView) view.findViewById(R.id.pickup_location_placename);

                //locationText.setText(userSnapshot.child("PickupLocation").child("latitude").getValue().toString() + "   " + userSnapshot.child("PickupLocation").child("longitude").getValue().toString());
                locationText.setText(getAddressFromLocation(getActivity(), (double) userSnapshot.child("PickupLocation").child("latitude").getValue(), (double) userSnapshot.child("PickupLocation").child("longitude").getValue()));
            }


        });
        return view;
    }
}
