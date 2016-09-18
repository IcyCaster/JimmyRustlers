package com.project.uoa.carpooling.fragments.carpool.passenger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.fragments.carpool.passenger.explorer.P_E_Offers;
import com.project.uoa.carpooling.fragments.carpool.passenger.explorer.P_E_Requests;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_PagerAdapter extends FragmentPagerAdapter {

    private int tabCount = 2;

    public P_PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new P_E_Requests();
            case 1:
                return new P_E_Offers();
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
                return "Request";
            case 1:
                return "Offers";
            default:
                return null;
        }
    }
}
