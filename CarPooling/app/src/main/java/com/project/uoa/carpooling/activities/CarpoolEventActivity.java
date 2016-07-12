package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.fragments.Event_RequestsAndOffers;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;
import com.project.uoa.carpooling.fragments.Event_Specifics_Offers;
import com.project.uoa.carpooling.fragments.Event_Specifics_Passengers;
import com.project.uoa.carpooling.fragments.Event_Specifics_Requests;

public class CarpoolEventActivity extends AppCompatActivity implements Event_Specifics_Passengers.OnFragmentInteractionListener, Event_Details.OnFragmentInteractionListener, Event_RequestsAndOffers.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener, Event_Specifics_Requests.OnFragmentInteractionListener, Event_Specifics_Offers.OnFragmentInteractionListener{

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

        setContentView(R.layout.activity_car_pool_event);

        // Get everything necessary from the MainActivity
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        eventID = bundle.getString("eventID");
        eventStatus = bundle.getString("eventStatus");

        // Set up the pageViewer with the adapter
        CarpoolEventPagerAdapter pagerAdapter = new CarpoolEventPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        // Add tabs to the pageViewer
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }
}
