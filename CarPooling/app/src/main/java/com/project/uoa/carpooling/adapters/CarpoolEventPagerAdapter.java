package com.project.uoa.carpooling.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.Event_Carpool;
import com.project.uoa.carpooling.fragments.Event_Details;
import com.project.uoa.carpooling.fragments.Event_Map;

/**
 * Created by Chester on 1/07/2016.
 */
public class CarpoolEventPagerAdapter extends FragmentPagerAdapter {

    Long eventId;

    public CarpoolEventPagerAdapter(FragmentManager fm, Long eventId) {
        super(fm);
        this.eventId = eventId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Event_Details.newInstance(eventId);
            case 1:
                return Event_Map.newInstance(eventId);
            case 2:
                return Event_Carpool.newInstance(eventId);

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
