package com.project.uoa.carpooling.carpoolevent.passenger.pages;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.carpoolevent.passenger.P_PagerAdapter;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_Explorer extends Fragment{

    private View view;
    private ViewPager viewPager;
    private P_PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_driver_explorer, container, false);

        pagerAdapter = new P_PagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.driver_explorer_viewpager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.driver_explorer_tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return view;
    }
}




