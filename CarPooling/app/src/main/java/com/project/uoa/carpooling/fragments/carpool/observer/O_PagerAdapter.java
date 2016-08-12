package com.project.uoa.carpooling.fragments.carpool.observer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
    private Context context;

    public O_PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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

        String title = "";
//        Drawable image = null;

        switch (position) {
            case 0:
                return title = "Drivers";
//                image = ContextCompat.getDrawable(context, R.drawable.driving_icon);
//                break;
            case 1:
                return title = "Passengers";
//                image = ContextCompat.getDrawable(context, R.drawable.passenger_icon);
//                break;
            default:
                return null;
//                break;
        }

        // BUG : SPANNABLE STRING FAILS IF ALLCAPS, TABLAYOUT CAN'T REMOVE ALLCAPS
//        Log.d("IMG", image.toString());
//        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//        SpannableString sb = new SpannableString("  " + title);
//        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
    }
}
