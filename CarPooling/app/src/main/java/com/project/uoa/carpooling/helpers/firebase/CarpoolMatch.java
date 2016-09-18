package com.project.uoa.carpooling.helpers.firebase;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * CarpoolMatch.TryJoin attempts to match a driver and passenger to form a carpool.
 * Usually they will match but several conditions must be checked before this can be confirmed.
 * It should be noted that Firebase deals with the possibility of race-conditions occurring where simultaneous matches happen.
 *
 * Conditions to check:
 * - Both are public
 * - Both have space (Drivers have room in their car, passengers already don't have a driver)
 * - One is still a driver and the other is still a passenger
 *
 * Created by Chester and Angel on 12/08/2016.
 */
public class CarpoolMatch {

    private static DatabaseReference fireBaseReference;
    private static Context context;

    public static void TryJoin(final String eventID, final String driverID, final String passengerID, final Context context, final Button confirmButton, final Button cancelButton) {
        CarpoolMatch.context = context;
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(
                new FirebaseValueEventListener() {

                    @Override
                    public void onDataChange(final DataSnapshot snapshot) {

                        // Check that both are public()
                        if (!(boolean) snapshot.child(driverID).child("isPublic").getValue() || !(boolean) snapshot.child(passengerID).child("isPublic").getValue()) {
                            MatchPopup("Oops!", "Something unexpected happened!", false);
                        } else {
                            // Make sure there is enough space in the car
                            int passengerSpaceAvailable = (int) (long) snapshot.child(driverID).child("Passengers").child("PassengerCapacity").getValue();
                            for (DataSnapshot child : snapshot.child(driverID).child("Passengers").getChildren()) {
                                if (!child.getKey().equals("PassengerCapacity")) {
                                    int totalPassengerCount = (int) (long) child.getValue();
                                    passengerSpaceAvailable -= totalPassengerCount;
                                }
                            }
                            final int passengerCount = (int) (long) snapshot.child(passengerID).child("PassengerCount").getValue();
                            if (passengerSpaceAvailable - passengerCount < 0) {
                                MatchPopup("Oops!", "There is no room!", false);
                            } else {
                                // Make sure passenger doesn't already have an allocated driver
                                if (!snapshot.child(passengerID).child("Driver").getValue().equals("null")) {
                                    MatchPopup("Oops!", "Passenger already has a driver!", false);
                                } else {

                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                    alert.setTitle("Confirm");
                                    alert.setMessage("Confirm that you approve this!");
                                    alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            // Set passengers value to false
                                            fireBaseReference.child("events").child(eventID).child("users").child(passengerID).child("isPublic").setValue(false);

                                            // Set driver in passengers ref
                                            fireBaseReference.child("events").child(eventID).child("users").child(passengerID).child("Driver").setValue(driverID);

                                            // Add passenger and count to driver
                                            fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("Passengers").child(passengerID).setValue(passengerCount);

                                            // Clean up requests and offers
                                            if(snapshot.child(driverID).child("Requests").child(passengerID).exists()) {
                                                fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("Requests").child(passengerID).removeValue();
                                            }
                                            if(snapshot.child(passengerID).child("Offers").child(driverID).exists()) {
                                                fireBaseReference.child("events").child(eventID).child("users").child(passengerID).child("Offers").child(driverID).removeValue();
                                            }

                                            // Resolve buttons
                                            confirmButton.setEnabled(false);
                                            cancelButton.setEnabled(false);

                                            dialog.cancel();
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
                        }
                    }
                }
        );
    }

    // Confirmation popup for confirming that they want to match
    private static void MatchPopup(String title, String message, boolean hasNegative) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        if (hasNegative) {
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }
        Dialog dialog = alert.create();
        dialog.show();
    }
}


