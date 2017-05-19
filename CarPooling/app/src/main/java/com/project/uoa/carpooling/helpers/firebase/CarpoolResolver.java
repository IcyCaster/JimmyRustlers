package com.project.uoa.carpooling.helpers.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;

/**
 * The Carpool Resolver takes the user's current status and the status they are changing to and resolves the backend Firebase updating.
 * Created by Chester on 21/08/2016.
 */
public class CarpoolResolver {
    private static DatabaseReference fireBaseReference;
    private static DatabaseReference usersFirebaseRef;
    private static EventStatus currentStatus;
    private static String userID;
    private static String eventID;
    private static int passengerAmount;
    private static Place place;
    private static CarpoolEventActivity context;

    public static void changeStatus(CarpoolEventActivity context, EventStatus desiredStatus, int passengerAmount, Place place) {

        // load firebase reference and values from the activity
        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        currentStatus = context.getEventStatus();
        eventID = context.getEventID();
        userID = context.getUserID();
        CarpoolResolver.context = context;
        CarpoolResolver.place = place;
        CarpoolResolver.passengerAmount = passengerAmount;

        // locate user in firebase
        usersFirebaseRef = fireBaseReference.child("events").child(eventID).child("users").child(userID);

        if (currentStatus == EventStatus.DRIVER && desiredStatus == EventStatus.PASSENGER) {
            Log.d("StatusChange", "Driver to Passenger");
            removeDriverComponents(false);
            addPassengerComponents();
        } else if (currentStatus == EventStatus.PASSENGER && desiredStatus == EventStatus.DRIVER) {
            Log.d("StatusChange", "Passenger to Driver");
            removePassengerComponents(false);
            addDriverComponents();
        } else if (desiredStatus == EventStatus.OBSERVER) {
            Log.d("StatusChange", "Something to observer");
            if (currentStatus == EventStatus.DRIVER) {
                removeDriverComponents(false);
            }

            if (currentStatus == EventStatus.PASSENGER) {
                removePassengerComponents(false);
            }
            usersFirebaseRef.child("Status").setValue("Observer");
            reloadActivity(false);
        } else if (currentStatus == EventStatus.OBSERVER) {
            Log.d("StatusChange", "Observer to something");
            if (desiredStatus == EventStatus.DRIVER) {
                addDriverComponents();
            }

            if (desiredStatus == EventStatus.PASSENGER) {
                addPassengerComponents();
            }

        }

        // Changes user's record of status
        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue(desiredStatus.toString());
    }

    public static void leaveCarpool(CarpoolEventActivity context) {
        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        currentStatus = context.getEventStatus();
        eventID = context.getEventID();
        userID = context.getUserID();
        CarpoolResolver.context = context;

        if (currentStatus == EventStatus.DRIVER) {
            removeDriverComponents(true);
        }

        if (currentStatus == EventStatus.PASSENGER) {
            removePassengerComponents(true);
        }

        if (currentStatus == EventStatus.OBSERVER) {
            leaving();
        }
    }

    // When a user becomes a driver, add these components to firebase
    // Blank starting Location || Blank current Location || No passengers + Passenger Capacity || Set status to Driver || Set isPublic to true || Set isDriving to false
    private static void addDriverComponents() {
        usersFirebaseRef.child("StartLocation").setValue(place);
        usersFirebaseRef.child("CurrentLocation").setValue(new Place(null, 0, 0));
        usersFirebaseRef.child("Passengers").child("PassengerCapacity").setValue(passengerAmount);
        usersFirebaseRef.child("Status").setValue("Driver");
        usersFirebaseRef.child("isPublic").setValue(true);
        usersFirebaseRef.child("isDriving").setValue(false);
        reloadActivity(false);
    }

    // When a user becomes a passenger, add these components to firebase
    // Blank associated Driver || Blank pickup location || Passengers amount || Set status to Passenger || Set isPublic to true
    private static void addPassengerComponents() {
        usersFirebaseRef.child("Driver").setValue("null");
        usersFirebaseRef.child("PickupLocation").setValue(place);
        usersFirebaseRef.child("PassengerCount").setValue(passengerAmount);
        usersFirebaseRef.child("Status").setValue("Passenger");
        usersFirebaseRef.child("isPublic").setValue(true);
        reloadActivity(false);
    }


    private static void removeDriverComponents(final boolean isLeaving) {

        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Remove any driver offers
                    if (child.child("Offers").child(userID).exists()) {
                        fireBaseReference.child("events").child(eventID).child("users").child(child.getKey()).child("Offers").child(userID).removeValue();
                    }
                }

                for (DataSnapshot passengerID : snapshot.child(userID).child("Passengers").getChildren()) {
                    if (!passengerID.getKey().toString().equals("PassengerCapacity")) {
                        // Notify current passengers that the driver has left
                        if (snapshot.child(passengerID.getKey().toString()).child("Driver").exists()) {
                            if (snapshot.child(passengerID.getKey().toString()).child("Driver").getValue().toString().equals(userID)) {
                                fireBaseReference.child("events").child(eventID).child("users").child(passengerID.getKey().toString()).child("Driver").setValue("abandoned");
                            }
                        }
                    }
                }
                // Remove driver stuff
                if (isLeaving) {
                    leaving();
                } else {

                    usersFirebaseRef.child("CurrentLocation").removeValue();
                    usersFirebaseRef.child("Passengers").removeValue();
                    usersFirebaseRef.child("StartLocation").removeValue();
                    usersFirebaseRef.child("isDriving").removeValue();
                    usersFirebaseRef.child("Requests").removeValue();
                }
            }
        });
    }

    private static void removePassengerComponents(final boolean isLeaving) {
        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Remove any passenger requests
                    if (child.child("Requests").child(userID).exists()) {
                        fireBaseReference.child("events").child(eventID).child("users").child(child.getKey()).child("Requests").child(userID).removeValue();
                    }
                }
                String driver = snapshot.child(userID).child("Driver").getValue().toString();
                Log.d("driver", driver);
                if (snapshot.child(driver).exists()) {
                    fireBaseReference.child("events").child(eventID).child("users").child(driver).child("Passengers").child(userID).setValue("abandoned");
                }


                // Remove passenger stuff
                if (isLeaving) {
                    leaving();
                } else {
                    usersFirebaseRef.child("PickupLocation").removeValue();
                    usersFirebaseRef.child("Driver").removeValue();
                    usersFirebaseRef.child("PassengerCount").removeValue();
                    usersFirebaseRef.child("Offers").removeValue();
                }
            }
        });
    }

    private static void leaving() {
        //remove from users
        usersFirebaseRef.removeValue();
        //remove from events
        usersFirebaseRef.removeValue();

        reloadActivity(true);
    }

    private static void becomingObserver() {
        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Save username for re-adding
                String usersName = snapshot.child("Name").getValue().toString();

                usersFirebaseRef.setValue("Observer");
                usersFirebaseRef.child("Name").setValue(usersName);
                usersFirebaseRef.child("Status").setValue("Observer");
                usersFirebaseRef.child("isPublic").setValue(false);

                reloadActivity(false);
            }
        });
    }

    private static void reloadActivity(boolean isLeaving) {
        Intent i = new Intent(context, CarpoolEventActivity.class);
        Bundle b = new Bundle();
        b.putString("userID", userID);
        b.putString("eventID", eventID);
        i.putExtras(b);

        context.finish();

        // Is the users isn't leaving the carpool then refresh the activity for updated information
        if (!isLeaving) {
            context.startActivity(i);
        }
    }
}
