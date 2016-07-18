package com.project.uoa.carpooling.carpoolevent.driver.explorer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.fragments.carpool.Explorer_Offers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Passengers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Requests;

/**
 * Created by Chester on 18/07/2016.
 */
public class DPagerAdapter extends FragmentPagerAdapter {

    private int tabCount = 3;

    public DPagerAdapter(FragmentManager fm, String eventStatus) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DOffers();
            case 1:
                return new DRequests();
            case 2:
                return new DPassengers();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Offers";
            case 1:
                return "Requests";
            case 2:
                return "Passengers";
            default:
                return null;
        }
    }
}
