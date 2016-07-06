package com.project.uoa.carpooling.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.fragments.Event_Carpool_Driver;
import com.project.uoa.carpooling.fragments.Event_Carpool_Observer;
import com.project.uoa.carpooling.fragments.Event_Carpool_Passenger;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;

/**
 * Created by Chester on 1/07/2016.
 */
public class CarpoolEventPagerAdapter extends FragmentStatePagerAdapter {

    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;
    private Long eventId;
    private String status;

    public CarpoolEventPagerAdapter(FragmentManager fm, Long eventId, MainActivity mainActivity) {
        super(fm);
        this.eventId = eventId;

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = mainActivity.getUserId();


       fetchStatus();

    }

    @Override
    public Fragment getItem(int position) {

        fetchStatus();

        switch (position) {
            case 0:
                return Event_Details.newInstance(eventId);
            case 1:
                return Event_Map.newInstance(eventId);
            case 2:
                if(status.equals("O")){
                    return Event_Carpool_Observer.newInstance(eventId);
                }
                else if(status.equals("P")){
                    return Event_Carpool_Passenger.newInstance(eventId);
                }
                else if(status.equals("D")){
                    return Event_Carpool_Driver.newInstance(eventId);
                }
                else {
                    return Event_Carpool_Observer.newInstance(eventId);
                }

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
//                return getString(R.string.someStringReferenceHere);
                return "Details";
            case 1:
                return "Map";
            case 2:
                return "Carpool";
            default:
                return null;
        }
    }

    public void fetchStatus() {


        // Checks DB/users/{user-id}
        fireBaseReference.child("users").child(userId).child(Long.toString(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // If it exists, everything is sweet
                if (snapshot.child("Status").getValue().equals("Observer")) {
                    Log.d("firebase - event", "You're an: Observer");
                    status = "O";
                }
                // If it doesn't, create the user in the Firebase database
                else if (snapshot.child("Status").getValue().equals("Driver")) {
                    Log.d("firebase - event", "You're a: Driver");
                    status = "D";
                } else if (snapshot.child("Status").getValue().equals("Passenger")) {
                    Log.d("firebase - event", "You're a: Passenger");
                    status = "P";
                } else {
                    Log.d("firebase - event", "Did not find it ");
                    status = "P";
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });
    }


}
