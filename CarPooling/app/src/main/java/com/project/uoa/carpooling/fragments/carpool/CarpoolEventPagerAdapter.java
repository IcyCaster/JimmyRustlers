package com.project.uoa.carpooling.fragments.carpool;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Details;
import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Explorer;
import com.project.uoa.carpooling.fragments.carpool.driver.pages.D_Map;
import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Details;
import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Explorer;
import com.project.uoa.carpooling.fragments.carpool.observer.pages.O_Map;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Details;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Explorer;
import com.project.uoa.carpooling.fragments.carpool.passenger.pages.P_Map;

/**
 * Created by Chester Booker and Angel Castro on 18/07/2016.
 */
public class CarpoolEventPagerAdapter extends FragmentStatePagerAdapter {

    private EventStatus eventStatus;
    private Context context;
    private MapsFragment mapsFragment;

    public CarpoolEventPagerAdapter(FragmentManager fm, EventStatus eventStatus, Context context) {
        super(fm);
        this.eventStatus = eventStatus;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (eventStatus) {
            case OBSERVER:
                switch (position) {
                    case 0:
                        return new O_Details();
                    case 1:
                        return new O_Explorer();
                    case 2:
                        return mapsFragment = new O_Map();
                }
            case DRIVER:
                switch (position) {
                    case 0:
                        return new D_Details();
                    case 1:
                        return new D_Explorer();
                    case 2:
                        return mapsFragment = new D_Map();
                }
            case PASSENGER:
                switch (position) {
                    case 0:
                        return new P_Details();
                    case 1:
                        return new P_Explorer();
                    case 2:
                        return mapsFragment = new P_Map();
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
                if (eventStatus == EventStatus.OBSERVER) {
                    return "People";
                } else if (eventStatus == EventStatus.DRIVER) {
                    return "Passengers";
                }
                if (eventStatus == EventStatus.PASSENGER) {
                    return "Drivers";
                }
            case 2:
                return "Map";

            default:
                return null;
        }
    }
}
