package com.project.uoa.carpooling.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.Event_Carpool;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;

/**
 * Created by Chester on 1/07/2016.
 */
public class FixedTabsPagerAdapter extends FragmentPagerAdapter {


    public FixedTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Event_Details ed = new Event_Details();
                return ed;
            case 1:
                Event_Map em = new Event_Map();
                return em;
            case 2:
                Event_Carpool ec = new Event_Carpool();
                return ec;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
//                return getString(R.string.someStringReferenceHere);
                return "Details";
            case 1:
                return "Map";
            case 2:
                return "Carpool";
            default:
                return null;
        }
    }


}
