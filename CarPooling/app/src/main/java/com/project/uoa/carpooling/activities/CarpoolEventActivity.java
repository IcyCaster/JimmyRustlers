package com.project.uoa.carpooling.activities;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.adapters.CarpoolEventPagerAdapterFORACTIVITY;
import com.project.uoa.carpooling.fragments.Event_Carpool_Observer;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;
import com.project.uoa.carpooling.fragments.Event_Offers;
import com.project.uoa.carpooling.fragments.Event_Requests;

public class CarpoolEventActivity extends AppCompatActivity implements Event_Details.OnFragmentInteractionListener, Event_Carpool_Observer.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener, Event_Requests.OnFragmentInteractionListener, Event_Offers.OnFragmentInteractionListener{

    private Long eventId;
    private ViewPager viewPager;
    private CarpoolEventPagerAdapterFORACTIVITY pagerAdapter;
    private View view;
    private String status;

    //Firebase things
    private DatabaseReference fireBaseReference;

    private String userId;

    public String getUserId() {
        return userId;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_car_pool_event_chesters);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userID");
        status = bundle.getString("status");
        Long eventId = bundle.getLong("eventID");

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fireBaseReference = FirebaseDatabase.getInstance().getReference();



        pagerAdapter = new CarpoolEventPagerAdapterFORACTIVITY(getSupportFragmentManager(), eventId, userId, status);

        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }
}
