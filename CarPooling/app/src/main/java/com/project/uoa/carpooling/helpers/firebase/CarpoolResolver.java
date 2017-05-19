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
 * The Carpool Resolver takes the user's current status and desired status, then resolves the backend Firebase updating.
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

    private static void loadContextValues(CarpoolEventActivity context){
        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        currentStatus = context.getEventStatus();
        eventID = context.getEventID();
        userID = context.getUserID();
        CarpoolResolver.context = context;
    }

    public static void changeStatus(CarpoolEventActivity context, EventStatus desiredStatus, int passengerAmount, Place place) {
        loadContextValues(context);
        CarpoolResolver.place = place;
        CarpoolResolver.passengerAmount = passengerAmount;

        // locate user in firebase
        usersFirebaseRef = fireBaseReference.child("events").child(eventID).child("users").child(userID);

        // Log the status changes
        Log.d("StatusChange", currentStatus.toString() + " to " + desiredStatus.toString());

        // remove the components associated with the current status
        switch(currentStatus) {
            case DRIVER:
                removeDriverComponents(false);
                break;
            case PASSENGER:
                removePassengerComponents(false);
                break;
            case OBSERVER:
                break;
            default:
                Log.e("Status Change", "Invalid Current Status");
        }

        // add the components associated with the desired status
        switch(desiredStatus) {
            case DRIVER:
                addDriverComponents();
                break;
            case PASSENGER:
                addPassengerComponents();
                break;
            case OBSERVER:
                usersFirebaseRef.child("Status").setValue("Observer");
                reloadActivity(false);
                break;
            default:
                Log.e("Status Change", "Invalid Desired Status");
        }
        // Changes user's record of status
        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue(desiredStatus.toString());
    }

    public static void leaveCarpool(CarpoolEventActivity context) {
        loadContextValues(context);
        switch(currentStatus) {
            case DRIVER:
                removeDriverComponents(true);
                break;
            case PASSENGER:
                removePassengerComponents(true);
                break;
            case OBSERVER:
                leaving();
                break;
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

    private static void removeDriverComponents(final boolean isLeaving) {
        fireBaseReference.child("events").child(eventID).child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Remove any driver offers made by the user
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Offers").child(userID).exists()) {
                        fireBaseReference.child("events").child(eventID).child("users").child(child.getKey()).child("Offers").child(userID).removeValue();
                    }
                }
                // Notify current passengers that the driver has left
                for (DataSnapshot passengerID : snapshot.child(userID).child("Passengers").getChildren()) {
                    if (!passengerID.getKey().toString().equals("PassengerCapacity")) {
                        if (snapshot.child(passengerID.getKey().toString()).child("Driver").exists()) {
                            if (snapshot.child(passengerID.getKey().toString()).child("Driver").getValue().toString().equals(userID)) {
                                fireBaseReference.child("events").child(eventID).child("users").child(passengerID.getKey().toString()).child("Driver").setValue("abandoned");
                            }
                        }
                    }
                }
                // Remove driver specific components
                if (isLeaving) {
                    leaving(); // Removes all components
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

                // Remove any passenger requests made
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("Requests").child(userID).exists()) {
                        fireBaseReference.child("events").child(eventID).child("users").child(child.getKey()).child("Requests").child(userID).removeValue();
                    }
                }

                // Remove the associated driver if one exists
                String driver = snapshot.child(userID).child("Driver").getValue().toString();
                Log.d("driver", driver);
                if (snapshot.child(driver).exists()) {
                    fireBaseReference.child("events").child(eventID).child("users").child(driver).child("Passengers").child(userID).setValue("abandoned");
                }
                // Remove passenger components
                if (isLeaving) {
                    leaving(); // Removes all components
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
        //remove event from users list
        fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
        //remove user from events list
        usersFirebaseRef.removeValue();
        reloadActivity(true);
    }

    // Reload the activity so the user's UI is refreshed with the changes
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
