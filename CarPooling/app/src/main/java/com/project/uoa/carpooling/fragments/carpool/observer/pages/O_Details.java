package com.project.uoa.carpooling.fragments.carpool.observer.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.fragments.carpool.DetailsFragment;

/**
 * O_Details is the first tab.
 * Shows only the event information.
 * * Created by Angel and Chester
 */
public class O_Details extends DetailsFragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.carpool_details_observer, container, false);
        super.addEventDetails(view);
        return view;
    }
}
