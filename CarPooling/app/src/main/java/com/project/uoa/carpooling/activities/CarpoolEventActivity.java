package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.jsonparsers.Facebook_ComplexEvent_Parser;
import com.project.uoa.carpooling.dialogs.ChangeStatusDialog;
import com.project.uoa.carpooling.fragments.carpool.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.dialogs.UpdateStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.fragments.carpool.Event_Map;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.view.View.OnClickListener;

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

    public FloatingActionsMenu floatingActionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

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
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
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

                                TextView statusText = (TextView)findViewById(R.id.status_text);
                                statusText.setText(eventStatus.toString());

                                // Create Pager and Adapter
                                CarpoolEventPagerAdapter pagerAdapter = new CarpoolEventPagerAdapter(getSupportFragmentManager(), eventStatus, getApplicationContext());
                                ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
                                viewPager.setAdapter(pagerAdapter);

                                // Add tabs to the pageViewer
                                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                                tabLayout.setupWithViewPager(viewPager);


                                tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.details_icon));
                                tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.map_icon));
                                tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.explorer_arrows_icon));


                                final RelativeLayout bg = (RelativeLayout) findViewById(R.id.semi_black_bg);
                                floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
                                final FloatingActionButton actionC = new FloatingActionButton(getBaseContext());

                                if(eventStatus == EventStatus.OBSERVER) {
                                    tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.explorer_o_icon));
                                }
                                else if(eventStatus == EventStatus.DRIVER) {
                                    actionC.setTitle("Manage Passengers");
                                    actionC.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//
                                        }
                                    });
                                    floatingActionsMenu.addButton(actionC);
                                }
                                else if(eventStatus == EventStatus.PASSENGER) {
                                    actionC.setTitle("Manage Driver");
                                    actionC.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//
                                        }
                                    });
                                    floatingActionsMenu.addButton(actionC);
                                }

                                final FloatingActionButton messagingButton = (FloatingActionButton) findViewById(R.id.messaging_button);
                                messagingButton.setColorNormalResId(R.color.colorAccent);
                                messagingButton.setColorPressedResId(R.color.colorAccentLight);
                                messagingButton.setIcon(R.drawable.ic_fab_star);
                                messagingButton.setStrokeVisible(false);

                                floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                                    @Override
                                    public void onMenuExpanded() {
                                        messagingButton.setVisibility(View.VISIBLE);
                                        bg.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onMenuCollapsed() {
                                        messagingButton.setVisibility(View.GONE);
                                        bg.setVisibility(View.GONE);
                                    }
                                });


                                final FloatingActionButton detailsButton = (FloatingActionButton) findViewById(R.id.details_fab);
                                detailsButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("Change Details", "BUTTON TODO:");
                                        detailsButton.setTitle("TODO");
                                    }
                                });

                                final FloatingActionButton leaveCarpoolButton = (FloatingActionButton) findViewById(R.id.leave_carpool_fab);
                                leaveCarpoolButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // TODO: Add an R U SURE? popup

                                        //remove from users
                                        fireBaseReference.child("users").child(userID).child("events").child(eventID).removeValue();
                                        //remove from events
                                        fireBaseReference.child("events").child(eventID).child("users").child(userID).removeValue();

                                        Log.d("firebase - event", "Unsubscribed: " + eventID);

                                        CarpoolEventActivity.this.finish();
                                    }
                                });

                                final FloatingActionButton changeStatusButton = (FloatingActionButton) findViewById(R.id.change_status_fab);
                                changeStatusButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Create and show the dialog.
                                        ChangeStatusDialog newFragment = new ChangeStatusDialog();
                                        newFragment.show(getSupportFragmentManager(), "status_dialog");
                                    }
                                });

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

    @Override
    public void onBackPressed() {
        if(floatingActionsMenu.isExpanded()) {
            floatingActionsMenu.collapse();
        }
        else {
            super.onBackPressed();
        }
    }
}
