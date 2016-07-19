package com.project.uoa.carpooling.carpoolevent.driver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.carpoolevent.driver.explorer.DriverOffers;
import com.project.uoa.carpooling.carpoolevent.driver.explorer.DriverRequests;

/**
 * Created by Chester on 18/07/2016.
 */
public class DriverPagerAdapter extends FragmentPagerAdapter {

    private int tabCount = 2;

    public DriverPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DriverOffers();
            case 1:
                return new DriverRequests();
//            case 2:
//                return new DPassengers();
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
//            case 2:
//                return "Passengers";
            default:
                return null;
        }
    }
}
