package com.project.uoa.carpooling.fragments.carpool;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Details;
import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Explorer;
import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Map;

import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Details;
import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Explorer;
import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Map;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Details;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Explorer;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Map;
import com.project.uoa.carpooling.enums.EventStatus;

/**
 * Created by Chester on 18/07/2016.
 */
public class CarpoolEventPagerAdapter extends FragmentStatePagerAdapter {

    private EventStatus eventStatus;

    public CarpoolEventPagerAdapter(FragmentManager fm, EventStatus eventStatus) {
        super(fm);
        this.eventStatus = eventStatus;
    }

    @Override
    public Fragment getItem(int position) {
        switch (eventStatus) {
            case OBSERVER:
                switch (position) {
                    case 0:
                        return new O_Details();
                    case 1:
                        return new Event_Map();
                    case 2:
                        return new O_Explorer();
                }
            case DRIVER:
                switch (position) {
                    case 0:
                        return new D_Details();
                    case 1:
                        return new Event_Map();
                    case 2:
                        return new D_Explorer();
                }
            case PASSENGER:
                switch (position) {
                    case 0:
                        return new P_Details();
                    case 1:
                        return new Event_Map();
                    case 2:
                        return new P_Explorer();
                }
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
                return "Details";
            case 1:
                return "Map";
            case 2:
                return "Explorer";
            default:
                return null;
        }
    }
}