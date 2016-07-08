package com.project.uoa.carpooling.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.fragments.Event_Carpool_Observer;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;

/**
 * Created by Chester on 1/07/2016.
 */
public class CarpoolEventPagerAdapterFORACTIVITY extends FragmentStatePagerAdapter {

    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;
    private Long eventId;
    private String status;

    public CarpoolEventPagerAdapterFORACTIVITY(FragmentManager fm, Long eventId, String userId, String status) {
        super(fm);

        this.status = status;

        // Splash screen saying "FETCHING STATUS?"

        this.eventId = eventId;

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        this.userId = userId;




    }

    @Override
    public Fragment getItem(int position) {



        Log.d("status", "You're an: " + status);
        switch (position) {
            case 0:
                return Event_Details.newInstance(eventId);
            case 1:
                return Event_Map.newInstance(eventId);
            case 2:
                    return Event_Carpool_Observer.newInstance(eventId,status);
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





}
