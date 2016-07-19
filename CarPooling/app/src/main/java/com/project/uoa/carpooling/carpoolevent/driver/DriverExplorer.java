package com.project.uoa.carpooling.carpoolevent.driver;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.enums.EventStatus;

/**
 * Created by Chester on 18/07/2016.
 */
public class DriverExplorer extends Fragment{

    // TODO: Rename and change types of parameters

    private EventStatus eventStatus;
    private DatabaseReference fireBaseReference;
    private String userId;
    private View view;


    private ViewPager viewPager;
    private DriverPagerAdapter pagerAdapter;
    private TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_driver_explorer, container, false);

        pagerAdapter = new DriverPagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.driver_explorer_viewpager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.driver_explorer_tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return view;
    }
}




