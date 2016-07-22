package com.project.uoa.carpooling.carpoolevent.driver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.uoa.carpooling.carpoolevent.driver.explorer.D_E_Offers;
import com.project.uoa.carpooling.carpoolevent.driver.explorer.D_E_Requests;

/**
 * Created by Chester on 18/07/2016.
 */
public class D_PagerAdapter extends FragmentPagerAdapter {

    private int tabCount = 2;

    public D_PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new D_E_Offers();
            case 1:
                return new D_E_Requests();
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
            default:
                return null;
        }
    }
}
