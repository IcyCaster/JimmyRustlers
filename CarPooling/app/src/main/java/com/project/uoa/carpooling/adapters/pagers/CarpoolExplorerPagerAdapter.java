package com.project.uoa.carpooling.adapters.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.fragments.carpool.Explorer_Offers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Passengers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Requests;

/**
 * Created by Chester on 8/07/2016.
 */
public class CarpoolExplorerPagerAdapter extends FragmentPagerAdapter {

    private String eventStatus;
    private int tabCount;

    public CarpoolExplorerPagerAdapter(FragmentManager fm, String eventStatus) {
        super(fm);
        this.eventStatus = eventStatus;

        // If they are a driver then the fragment needs to list requests, offers and passengers
        if (eventStatus.equals("Driver")) {
            tabCount = 3;
        } else {
            tabCount = 2;
        }
    }

    @Override
    public Fragment getItem(int position) {

        if (eventStatus.equals("Driver")) {
            switch (position) {
                case 0:
                    return new Explorer_Offers();
                case 1:
                    return new Explorer_Requests();
                case 2:
                    return new Explorer_Passengers();
                default:
                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    return new Explorer_Offers();
                case 1:
                    return new Explorer_Requests();
                default:
                    return null;
            }
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (eventStatus.equals("Driver")) {
            switch (position) {
                case 0:
                    //TODO: Same as other adapter
                    return "Offers";
                case 1:
                    return "Requests";
                case 2:
                    return "Passengers";
                default:
                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    //TODO: Same as other adapter
                    return "Offers";
                case 1:
                    return "Requests";
                default:
                    return null;
            }
        }
    }
}
