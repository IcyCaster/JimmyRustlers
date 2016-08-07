package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.carpool.driver.D_PagerAdapter;
import com.project.uoa.carpooling.fragments.carpool.observer.O_PagerAdapter;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_Explorer extends Fragment{

    private View view;
    private ViewPager viewPager;
    private O_PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_explorer_tab_pager, container, false);

        pagerAdapter = new O_PagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.driver_explorer_viewpager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.driver_explorer_tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return view;
    }
}




