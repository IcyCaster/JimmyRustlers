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


public class O_Details extends DetailsFragment {

    private DatabaseReference fireBaseReference;

    private String eventID;
    private String userID;


    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.carpool_details_observer, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();

        super.addEventDetails(view);


        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        return view;
    }
}
