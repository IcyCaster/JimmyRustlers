package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.adapters.RequestsAndOffersPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_RequestsAndOffers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_RequestsAndOffers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_RequestsAndOffers extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String EVENT_ID = "param1";
    private static final String EVENT_STATUS = "param2";

    // TODO: Rename and change types of parameters
    private Long eventId;
    private String status;
    private String NOTUSEDstatus;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;
    private View view;


    private ViewPager viewPager;
    private RequestsAndOffersPagerAdapter pagerAdapter;
    private TabLayout tabLayout;


    private OnFragmentInteractionListener mListener;

    public Event_RequestsAndOffers() {
        // Required empty public constructor
    }

    public static Event_RequestsAndOffers newInstance(Long eventId, String status) {
        Event_RequestsAndOffers fragment = new Event_RequestsAndOffers();

        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        args.putString(EVENT_STATUS, status);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(EVENT_ID);
            NOTUSEDstatus = getArguments().getString(EVENT_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        status = ((CarpoolEventActivity)getActivity()).getEventStatus();

        view = inflater.inflate(R.layout.fragment_event_requests_and_offers, container, false);

        Snackbar.make(container, "Test get status " + status, Snackbar.LENGTH_LONG).setAction("Action", null).show();



        viewPager = (ViewPager) view.findViewById(R.id.view_pager1);

        pagerAdapter = new RequestsAndOffersPagerAdapter(getChildFragmentManager(), status);
        viewPager.setAdapter(pagerAdapter);


        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout1);


        //Notice how The Tab Layout adn View Pager object are linked
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}


