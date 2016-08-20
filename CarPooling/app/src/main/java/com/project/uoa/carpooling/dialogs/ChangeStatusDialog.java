package com.project.uoa.carpooling.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

/**
 * Created by Chester on 12/07/2016.
 */
public class ChangeStatusDialog extends DialogFragment {

    private View view;

    private Button observerButton = null;
    private Button driverButton = null;
    private Button passengerButton = null;

    private DatabaseReference fireBaseReference;

    private String userID;
    private EventStatus eventStatus;
    private String eventID;

    public ChangeStatusDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set no title bar:
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        eventStatus = ((CarpoolEventActivity) getActivity()).getEventStatus();
        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        view = inflater.inflate(R.layout.fragment_status_selection, container, false);

        // If they are a passenger/driver they can only become an observer
        if (eventStatus == EventStatus.PASSENGER || eventStatus == EventStatus.DRIVER) {
            observerButton = (Button) view.findViewById(R.id.button1);

            // Hide the other button
            Button button = (Button) view.findViewById(R.id.button2);
            button.setVisibility(View.GONE);

            observerButton.setText("Change to: Observer");
            observerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // Launch an alert to double check with the user.
                    becomingObserverAlert();
                }
            });
        } else {
            driverButton = (Button) view.findViewById(R.id.button1);
            driverButton.setText("Change to: Driver");
            driverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("status_dialog2");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    UpdateStatusDialog statusFragment = UpdateStatusDialog.newInstance("Driver");
                    statusFragment.show(ft, "status_dialog2");


                }
            });

            passengerButton = (Button) view.findViewById(R.id.button2);
            passengerButton.setText("Change to: Passenger");
            passengerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("status_dialog2");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    UpdateStatusDialog statusFragment = UpdateStatusDialog.newInstance("Passenger");
                    statusFragment.show(ft, "status_dialog2");
                }
            });
        }
        return view;
    }

    public void becomingObserverAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Are you sure?");
        alert.setMessage("Do you want to no longer be a " + eventStatus.toString() + "? " + "Everything organised will be removed and those affected will be notified!");
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


                // TODO: Remove all old details. Somehow notify those affected.
                fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // Save username for re-adding
                        String usersName = snapshot.child("Name").getValue().toString();

                        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Observer");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();

                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Name").setValue(usersName);
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Observer");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue(false);


                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        Dialog dialog = alert.create();
        dialog.show();
    }
}
