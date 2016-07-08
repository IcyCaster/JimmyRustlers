package com.project.uoa.carpooling.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.fragments.Event_Specifics_Offers;
import com.project.uoa.carpooling.fragments.Event_Specifics_Passengers;
import com.project.uoa.carpooling.fragments.Event_Specifics_Requests;

/**
 * Created by Chester on 8/07/2016.
 */
public class RequestsAndOffersPagerAdapter extends FragmentPagerAdapter {

    private String status;
    private int tabCount;

    public RequestsAndOffersPagerAdapter(FragmentManager fm, String status) {
        super(fm);
        this.status = status;

        if (status.equals("Driver")) {
            tabCount = 3;
        } else {
            tabCount = 2;
        }
    }

    @Override
    public Fragment getItem(int position) {

        if (status.equals("Driver")) {
            switch (position) {
                case 0:
                    return new Event_Specifics_Offers();
                case 1:
                    return new Event_Specifics_Requests();
                case 2:
                    return new Event_Specifics_Passengers();
                default:
                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    return new Event_Specifics_Offers();
                case 1:
                    return new Event_Specifics_Requests();
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

        if (status.equals("Driver")) {
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
