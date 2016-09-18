package com.project.uoa.carpooling.fragments.carpool.driver.pages;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.carpool.driver.D_PagerAdapter;

/**
 * Driver fragment for showing passenger requests and offers.
 *
 * Created by Chester Booker and Angel Castro on 18/07/2016.
 */
public class D_Explorer extends Fragment{

    private View view;
    private ViewPager viewPager;
    private D_PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_explorer_tab_pager, container, false);

        pagerAdapter = new D_PagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.explorer_viewpager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.explorer_tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return view;
    }
}




