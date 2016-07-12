package com.project.uoa.carpooling.dialogs;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.adapters.CarpoolEventPagerAdapter;
import com.project.uoa.carpooling.adapters.StatusPagerAdapter;

/**
 * Created by Chester on 12/07/2016.
 */
public class ChangeStatusPopup extends DialogFragment {

    View view;
    String status;

    public ChangeStatusPopup(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.popup__change_status, container, false);

        // Set up the pageViewer with the adapter
       StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(getActivity().getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);


        return view;
    }
}
