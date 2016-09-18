package com.project.uoa.carpooling.fragments.carpool;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;

/**
 * Created by Chester Booker and Angel Castro on 10/08/2016.
 */
public class ExplorerFragment extends Fragment {

    public void setupTabViewPager(View view, PagerAdapter pagerAdapter) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.explorer_viewpager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.explorer_tab);
        tabLayout.setupWithViewPager(viewPager);
    }
}
