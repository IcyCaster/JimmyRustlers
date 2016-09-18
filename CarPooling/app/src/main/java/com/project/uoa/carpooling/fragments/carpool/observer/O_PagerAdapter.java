package com.project.uoa.carpooling.fragments.carpool.observer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.carpool.observer.explorer.O_E_Drivers;
import com.project.uoa.carpooling.fragments.carpool.observer.explorer.O_E_Passengers;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_PagerAdapter extends FragmentPagerAdapter {

    private int tabCount = 2;

    private String dText = "Drivers";
    private String pText = "Passengers";

    public O_PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void updatePageTitle(int position, String amount) {
        if(position == 0) {
            dText = "Drivers(" + amount + ")";
        }
        else{
            pText = "Passengers(" + amount + ")";
        }

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new O_E_Drivers();
            case 1:
                return new O_E_Passengers();
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
                return dText;
            case 1:
                return pText;
            default:
                return null;
        }
    }
}
