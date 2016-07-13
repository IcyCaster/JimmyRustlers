package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.pagers.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.dialogs.UpdateStatusDialog;
import com.project.uoa.carpooling.fragments.carpool.Event_Details;
import com.project.uoa.carpooling.fragments.carpool.Event_Map;
import com.project.uoa.carpooling.fragments.carpool.Event_Explorer;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Offers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Passengers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Requests;

public class CarpoolEventActivity extends AppCompatActivity implements UpdateStatusDialog.OnFragmentInteractionListener, Explorer_Passengers.OnFragmentInteractionListener, Event_Details.OnFragmentInteractionListener, Event_Explorer.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener, Explorer_Requests.OnFragmentInteractionListener, Explorer_Offers.OnFragmentInteractionListener {

    // These are relevant to the event instance
    private String userID;
    private String eventID;
    private String eventStatus;

    public String getUserID() {
        return userID;
    }
    public String getEventID() {
        return eventID;
    }
    public String getEventStatus() {
        return eventStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get userID and id passed from the MainActivity
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        eventID = bundle.getString("eventID");

        DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // users/{user-ID}/events/{event-ID}
        fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                eventStatus = snapshot.getValue().toString();

                setContentView(R.layout.activity__car_pool_instance);

                // Set up the pageViewer with the adapter
                CarpoolEventPagerAdapter pagerAdapter = new CarpoolEventPagerAdapter(getSupportFragmentManager());
                ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
                viewPager.setAdapter(pagerAdapter);

                // Add tabs to the pageViewer
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });
    }
    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }
}
