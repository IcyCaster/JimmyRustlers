package com.project.uoa.carpooling.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.project.uoa.carpooling.dialogs.UpdateStatusDialog;
import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.fragments.carpool.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.fragments.carpool.Event_Map;
import com.project.uoa.carpooling.fragments.main.SimpleMessenger;
import com.project.uoa.carpooling.helpers.firebase.CarpoolResolver;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

/**
 * CarpoolEventActivity is the created when a user wishes to see a specific carpool they have subscribed to.
 * These is a floating action button which provides them with the ability to message other members, leave the carpool, manage their driver/passengers and change their current details.
 * <p/>
 * From this activity they can navigate via tabbed pages to visit:
 * - The event/carpool details
 * - An explorer where they can check out passengers/drivers' requests/offers
 * They can also switch between the different status' Observer/Driver/Passenger
 * Or leave the carpool altogether.
 * - A map of the route to the location
 * <p/>
 * * Created by Angel and Chester
 */
public class CarpoolEventActivity extends AppCompatActivity implements UpdateStatusDialog.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener {

    // Unique to the event instance
    private String userID;
    private String eventID;
    private EventStatus eventStatus;

    private ComplexEventEntity facebookEventObject;
//    private Place eventLocation;

    private DatabaseReference fireBaseReference;

    private CarpoolEventPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private FloatingActionsMenu floatingActionsMenu;
    private OnClickListener changeStatus = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Create and show the dialog.
            ChangeStatusDialog newFragment = new ChangeStatusDialog();
            newFragment.show(getSupportFragmentManager(), "status_dialog");
        }
    };

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Facebook and Firebase
        FacebookSdk.sdkInitialize(getApplicationContext());
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Update values if savedInstanceState exists.
        if (savedInstanceState != null) {
            userID = savedInstanceState.getString("USER_ID");
            eventID = savedInstanceState.getString("EVENT_ID");
            eventStatus = (EventStatus) savedInstanceState.getSerializable("EVENT_STATUS");
            facebookEventObject = savedInstanceState.getParcelable("FACEBOOK_ENTITY");
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

                        // Save location as separate entity.
                        // eventLocation = facebookEventObject.getLocation();

                        // users/{user-ID}/events/{event-ID}
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                String status = snapshot.getValue().toString();
                                Log.d("firebase - log", "Event Status: " + status);

                                if (status.equals("Observer")) {
                                    eventStatus = EventStatus.OBSERVER;
                                } else if (status.equals("Driver")) {
                                    eventStatus = EventStatus.DRIVER;
                                } else if (status.equals("Passenger")) {
                                    eventStatus = EventStatus.PASSENGER;
                                }

                                // Set the activities view content
                                setContentView(R.layout.activity__car_pool_instance);

                                TextView statusText = (TextView) findViewById(R.id.status_text);
                                statusText.setText("ROLE: " + eventStatus.toString());
                                statusText.setOnClickListener(changeStatus);

                                Drawable img = ContextCompat.getDrawable(CarpoolEventActivity.this, R.drawable.icon_white_role_edit);
                                img.setBounds(0, 0, 60, 60);
                                statusText.setCompoundDrawables(null, null, img, null);

                                // Create Pager and Adapter
                                pagerAdapter = new CarpoolEventPagerAdapter(getSupportFragmentManager(), eventStatus, getApplicationContext());
                                viewPager = (ViewPager) findViewById(R.id.view_pager);
                                viewPager.setAdapter(pagerAdapter);

                                // Add tabs to the pageViewer
                                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                                tabLayout.setupWithViewPager(viewPager);

                                tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_white_details));
                                tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_white_observer));
                                tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_white_map));

                                // Add the floating action menu
                                final RelativeLayout bg = (RelativeLayout) findViewById(R.id.semi_black_bg);
                                floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

                                // Messaging Button
                                final FloatingActionButton messagingButton = (FloatingActionButton) findViewById(R.id.messaging_button);
                                messagingButton.setColorNormalResId(R.color.colorAccent);
                                messagingButton.setColorPressedResId(R.color.colorAccentLight);
                                messagingButton.setIcon(R.drawable.icon_black_messenger);
                                messagingButton.setStrokeVisible(false);
                                messagingButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Show the messaging dialog
                                        FragmentManager fm = getSupportFragmentManager();
                                        SimpleMessenger dFragment = new SimpleMessenger();
                                        dFragment.show(fm, "Messenger Fragment");
                                    }
                                });

                                // Show the messaging button when the floatingActionMenu is expanded and hide it when it is pressed again
                                floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                                    @Override
                                    public void onMenuExpanded() {
                                        if (!(eventStatus == EventStatus.OBSERVER)) {
                                            messagingButton.setVisibility(View.VISIBLE);
                                        }
                                        bg.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onMenuCollapsed() {
                                        messagingButton.setVisibility(View.GONE);
                                        bg.setVisibility(View.GONE);
                                    }
                                });

                                // Manager Button
                                final FloatingActionButton managerButton = new FloatingActionButton(getBaseContext());
                                // If driver, manage passengers
                                if (eventStatus == EventStatus.DRIVER) {
                                    managerButton.setTitle("Manage Passengers");
                                    managerButton.setIcon(R.drawable.icon_white_passenger);
                                    managerButton.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//
                                        }
                                    });
                                    floatingActionsMenu.addButton(managerButton);
                                    // If passenger, manage the driver
                                } else if (eventStatus == EventStatus.PASSENGER) {
                                    managerButton.setTitle("Manage Driver");
                                    managerButton.setIcon(R.drawable.icon_white_driver);
                                    managerButton.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//
                                        }
                                    });
                                    floatingActionsMenu.addButton(managerButton);
                                } else if (eventStatus == EventStatus.OBSERVER) {
                                    // If observer, don't show
                                    messagingButton.setVisibility(View.GONE);
                                }

                                // If not an observer, provide the ability for the user to change their capacity/location
                                if (!(eventStatus == EventStatus.OBSERVER)) {
                                    final FloatingActionButton detailsButton = (FloatingActionButton) findViewById(R.id.details_fab);
                                    detailsButton.setVisibility(View.VISIBLE);
                                    detailsButton.setIcon(R.drawable.icon_white_change_details);
                                    detailsButton.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.d("TODO", "This button is suppose to change the user's details when they are in a carpool");
                                            detailsButton.setTitle("TODO:");
                                        }
                                    });
                                }

                                // Leave carpool button
                                final FloatingActionButton leaveCarpoolButton = (FloatingActionButton) findViewById(R.id.leave_carpool_fab);
                                leaveCarpoolButton.setIcon(R.drawable.icon_white_leave);
                                leaveCarpoolButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        // Provide a secondary alert which double checks they want to leave the carpool
                                        AlertDialog.Builder alert = new AlertDialog.Builder(CarpoolEventActivity.this);
                                        alert.setTitle("Confirm leaving?");
                                        alert.setMessage(Html.fromHtml("Anything you have organised will be gone. You <b>cannot</b> undo this!"));
                                        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                                CarpoolResolver.leaveCarpool(CarpoolEventActivity.this);
                                                Log.d("firebase - event", "Unsubscribed: " + eventID);

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
                                });

                                final FloatingActionButton changeStatusButton = (FloatingActionButton) findViewById(R.id.change_status_fab);
                                changeStatusButton.setIcon(R.drawable.icon_white_change_status);
                                changeStatusButton.setOnClickListener(changeStatus);
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
        savedInstanceState.putSerializable("EVENT_STATUS", eventStatus);
        savedInstanceState.putString("USER_ID", userID);
        savedInstanceState.putString("EVENT_ID", eventID);
        savedInstanceState.putParcelable("FACEBOOK_ENTITY", facebookEventObject);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        eventStatus = (EventStatus) savedInstanceState.getSerializable("EVENT_STATUS");
        userID = savedInstanceState.getString("USER_ID");
        eventID = savedInstanceState.getString("EVENT_ID");
        facebookEventObject = savedInstanceState.getParcelable("FACEBOOK_ENTITY");
    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }

    // Close floating action menu if back button is pressed instead of backing out of the carpool activity
    @Override
    public void onBackPressed() {
        if (floatingActionsMenu.isExpanded()) {
            floatingActionsMenu.collapse();
        } else {
            super.onBackPressed();
        }
    }
}