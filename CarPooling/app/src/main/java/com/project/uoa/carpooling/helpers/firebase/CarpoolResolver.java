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
 * Created by Chester on 21/08/2016.
 */
public class CarpoolResolver {
    private static DatabaseReference fireBaseReference;
    private static EventStatus eventStatus;
    private static String userID;
    private static String eventID;
    private static int passengerAmount;
    private static Place place;
    private static CarpoolEventActivity context;

    public static void statusChange(CarpoolEventActivity context, EventStatus goToStatus, int passengerAmount, Place place) {
        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        eventStatus = context.getEventStatus();
        eventID = context.getEventID();
        userID = context.getUserID();
        CarpoolResolver.context = context;
        CarpoolResolver.place = place;
        CarpoolResolver.passengerAmount = passengerAmount;

        if(eventStatus==EventStatus.DRIVER && goToStatus==EventStatus.PASSENGER) {
            Log.d("StatusChange", "Driver to Passenger");
            removeDriverComponents(false);
            addPassengerComponents();
        }
        else if(eventStatus==EventStatus.PASSENGER && goToStatus==EventStatus.DRIVER) {
            Log.d("StatusChange", "Passenger to Driver");
            removePassengerComponents(false);
            addDriverComponents();
        }
        else if(goToStatus==EventStatus.OBSERVER) {
            Log.d("StatusChange", "Something to observer");
            if(eventStatus==EventStatus.DRIVER) {
                removeDriverComponents(false);
            }

            if(eventStatus==EventStatus.PASSENGER) {
                removePassengerComponents(false);
            }
            fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Observer");
            reloadActivity(false);
        }
        else if(eventStatus==EventStatus.OBSERVER) {
            Log.d("StatusChange", "Observer to something");
            if(goToStatus==EventStatus.DRIVER) {
                addDriverComponents();
            }

            if(goToStatus==EventStatus.PASSENGER) {
                addPassengerComponents();
            }

        }
        
        // Changes user's record of status
        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue(goToStatus.toString());
    }

    public static void leaveCarpool(CarpoolEventActivity context) {
        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        eventStatus = context.getEventStatus();
        eventID = context.getEventID();
        userID = context.getUserID();
        CarpoolResolver.context = context;

        if(eventStatus==EventStatus.DRIVER) {
            removeDriverComponents(true);
        }

        if(eventStatus==EventStatus.PASSENGER) {
            removePassengerComponents(true);
        }

        if(eventStatus==EventStatus.OBSERVER) {
            leaving();
        }
    }

    private static void addDriverComponents() {
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLocation").setValue(place);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLocation").setValue(new Place(null, 0,0));
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Passengers").child("PassengerCapacity").setValue(passengerAmount);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Driver");
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue(true);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").setValue(false);
        reloadActivity(false);
    }

    private static void addPassengerComponents() {
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Driver").setValue("null");
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLocation").setValue(place);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PassengerCount").setValue(passengerAmount);
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Passenger");
        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue(true);
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
                    if(!passengerID.getKey().toString().equals("PassengerCapacity")) {
                        // Notify current passengers that the driver has left
                        if (snapshot.child(passengerID.getKey().toString()).child("Driver").exists()) {
                            if (snapshot.child(passengerID.getKey().toString()).child("Driver").getValue().toString().equals(userID)) {
                                fireBaseReference.child("events").child(eventID).child("users").child(passengerID.getKey().toString()).child("Driver").setValue("abandoned");
                            }
                        }
                    }
                }
                // Remove driver stuff
                if(isLeaving) {
                    leaving();
                }
                else {
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLocation").removeValue();
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Passengers").removeValue();
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLocation").removeValue();
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").removeValue();
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Requests").removeValue();
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
                if(isLeaving) {
                    leaving();
                }
                else {
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLocation").removeValue();
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Driver").removeValue();
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PassengerCount").removeValue();
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Offers").removeValue();
                }
            }
        });
    }

    private static void leaving() {
        //remove from users
        fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
        //remove from events
        fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();

        reloadActivity(true);
    }

    private static void becomingObserver() {
        fireBaseReference.child("events").child(eventID).child("users").child(userID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Save username for re-adding
                String usersName = snapshot.child("Name").getValue().toString();



                fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Observer");

                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Name").setValue(usersName);
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Observer");
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue(false);

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

        if(!isLeaving) {
            context.startActivity(i);
        }
    }
}
