package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.project.uoa.carpooling.adapters.pagers.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.dialogs.UpdateStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.enums.EventStatus;
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
    private EventStatus eventStatus;
    private ComplexEventEntity facebookEventObject;
    private String status;

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

        FacebookSdk.sdkInitialize(getApplicationContext());
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Get userID and id passed from the MainActivity
        if (savedInstanceState != null) {
            userID = savedInstanceState.getString("USER_ID");
            eventID = savedInstanceState.getString("EVENT_ID");
            status = savedInstanceState.getString("EVENT_STATUS");
        } else {
            Bundle bundle = getIntent().getExtras();
            userID = bundle.getString("userID");
            eventID = bundle.getString("eventID");
            status = bundle.getString("eventStatus");
        }

        if(status.equals("Observer")) {
            eventStatus = EventStatus.OBSERVER;
        }
        else  if(status.equals("Driver")) {
            eventStatus = EventStatus.DRIVER;
        }
        else if(status.equals("Passenger")) {
            eventStatus = EventStatus.PASSENGER;
        }
        else {
            Log.e("Status Error", "Status was not assigned correctly");
        }

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + eventID,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        facebookEventObject = Facebook_ComplexEvent_Parser.parse(response.getJSONObject());

                        // users/{user-ID}/events/{event-ID}
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                // Not sure what the purpose of this is?
                                status = snapshot.getValue().toString();

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
                });
        request.executeAsync();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("USER_ID", userID);
        savedInstanceState.putString("EVENT_ID", eventID);
        savedInstanceState.putString("EVENT_STATUS", status);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        userID = savedInstanceState.getString("USER_ID");
        eventID = savedInstanceState.getString("EVENT_ID");
        status = savedInstanceState.getString("EVENT_STATUS");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }
}
