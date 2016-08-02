package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_ComplexEvent_Parser;
import com.project.uoa.carpooling.fragments.carpool.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.dialogs.UpdateStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.fragments.carpool.Event_Map;

/**
 * CarpoolEventActivity is the created when a user wishes to see a specific carpool they have subscribed to.
 * From this activity they can navigate via tabbed pages to visit:
 *                                                                 - The event/carpool details
 *                                                                 - A map of the route to the location
 *                                                                 - An explorer where they can check out passengers/drivers' requests/offers
 *
 * They can also switch between the different status' Observer/Driver/Passenger
 * Or leave the carpool altogether.
 */
public class CarpoolEventActivity extends AppCompatActivity implements UpdateStatusDialog.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener {
//TODO: Remove the irrelevant OnFragmentInteractionListeners

    // Unique to the event instance
    private String userID;
    private String eventID;
    private EventStatus eventStatus;
    private ComplexEventEntity facebookEventObject;

    private DatabaseReference fireBaseReference;

    public String getUserID() {
        return userID;
    }
    public String getEventID() {
        return eventID;
    }
    public EventStatus getEventStatus() {
        return eventStatus;
    }
    public ComplexEventEntity getFacebookEvent() {
        return facebookEventObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook and Firebase
        FacebookSdk.sdkInitialize(getApplicationContext());
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Set the userID and eventID
        if (savedInstanceState != null) {
            userID = savedInstanceState.getString("USER_ID");
            eventID = savedInstanceState.getString("EVENT_ID");
        } else {
            Bundle bundle = getIntent().getExtras();
            userID = bundle.getString("userID");
            eventID = bundle.getString("eventID");
        }

        // Facebook request to create the Facebook event object
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + eventID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        // Parsing the event object
                        facebookEventObject = Facebook_ComplexEvent_Parser.parse(response.getJSONObject());

                        // users/{user-ID}/events/{event-ID}
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                // Set the activities view content
                                setContentView(R.layout.activity__car_pool_instance);

                                String status = snapshot.getValue().toString();
                                Log.d("firebase - log", "Event Status: " + status);

                                if(status.equals("Observer")) {
                                    eventStatus = EventStatus.OBSERVER;
                                }
                                else if(status.equals("Driver")) {
                                    eventStatus = EventStatus.DRIVER;
                                }
                                else if(status.equals("Passenger")) {
                                    eventStatus = EventStatus.PASSENGER;
                                }

                                // Create Pager and Adapter
                                CarpoolEventPagerAdapter pagerAdapter = new CarpoolEventPagerAdapter(getSupportFragmentManager(), eventStatus);
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
                });
        // Execute Facebook request
        request.executeAsync();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save state members in saved instance
        savedInstanceState.putString("USER_ID", userID);
        savedInstanceState.putString("EVENT_ID", eventID);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        userID = savedInstanceState.getString("USER_ID");
        eventID = savedInstanceState.getString("EVENT_ID");
    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }
}
