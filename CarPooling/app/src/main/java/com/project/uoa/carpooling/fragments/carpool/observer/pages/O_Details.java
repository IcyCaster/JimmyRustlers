package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.dialogs.ChangeStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.fragments.carpool.DetailsFragment;


public class O_Details extends DetailsFragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;


    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_details_observer, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        super.addEventDetails(view);


        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // TODO: Make a helper, DisplayEventDetails(view, facebookEvent), as this can be called by Observer, Driver and Passenger
        // TODO: Reuse a details section kappa





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
