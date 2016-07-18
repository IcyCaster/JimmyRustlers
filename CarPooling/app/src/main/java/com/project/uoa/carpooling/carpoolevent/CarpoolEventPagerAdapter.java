package com.project.uoa.carpooling.carpoolevent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.uoa.carpooling.carpoolevent.driver.DriverDetails;
import com.project.uoa.carpooling.carpoolevent.driver.DriverExplorer;
import com.project.uoa.carpooling.carpoolevent.driver.DriverMap;
import com.project.uoa.carpooling.carpoolevent.observer.ObserverDetails;
import com.project.uoa.carpooling.carpoolevent.observer.ObserverExplorer;
import com.project.uoa.carpooling.carpoolevent.observer.ObserverMap;
import com.project.uoa.carpooling.carpoolevent.passenger.PassengerDetails;
import com.project.uoa.carpooling.carpoolevent.passenger.PassengerExplorer;
import com.project.uoa.carpooling.carpoolevent.passenger.PassengerMap;
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
                        return new ObserverDetails();
                    case 1:
                        return new ObserverMap();
                    case 2:
                        return new ObserverExplorer();
                }
            case DRIVER:
                switch (position) {
                    case 0:
                        return new DriverDetails();
                    case 1:
                        return new DriverMap();
                    case 2:
                        return new DriverExplorer();
                }
            case PASSENGER:
                switch (position) {
                    case 0:
                        return new PassengerDetails();
                    case 1:
                        return new PassengerMap();
                    case 2:
                        return new PassengerExplorer();
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
