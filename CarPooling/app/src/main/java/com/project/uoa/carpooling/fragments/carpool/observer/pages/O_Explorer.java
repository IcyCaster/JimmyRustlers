package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool.ExplorerFragment;
import com.project.uoa.carpooling.fragments.carpool.observer.O_PagerAdapter;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_Explorer extends ExplorerFragment{

    private View view;
    private ViewPager viewPager;
    private O_PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_explorer_tab_pager, container, false);
        pagerAdapter = new O_PagerAdapter(getChildFragmentManager());

        setupTabViewPager(view, pagerAdapter);

        return view;
    }




}




