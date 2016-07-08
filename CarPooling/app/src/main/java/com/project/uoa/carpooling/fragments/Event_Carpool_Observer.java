package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.project.uoa.carpooling.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_Carpool_Observer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_Carpool_Observer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Carpool_Observer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String EVENT_ID = "param1";
    private static final String EVENT_STATUS = "param2";

    // TODO: Rename and change types of parameters
    private Long eventId;
    private String status;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;
private View view;


    private ViewPager viewPager;
private YourPagerAdapter pagerAdapter;
    private TabLayout tabLayout;




    private OnFragmentInteractionListener mListener;

    public Event_Carpool_Observer() {
        // Required empty public constructor
    }

    public static Event_Carpool_Observer newInstance(Long eventId, String status) {
        Event_Carpool_Observer fragment = new Event_Carpool_Observer();

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
            status = getArguments().getString(EVENT_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




       view = inflater.inflate(R.layout.fragment_event_carpool_observer, container, false);

        Snackbar.make(container, "Test get status " + status, Snackbar.LENGTH_LONG).setAction("Action", null).show();

//        // Connect to Firebase
//        fireBaseReference = FirebaseDatabase.getInstance().getReference();
//
//        // Initialise shared preferences
//        userId = ((MainActivity)getActivity()).getUserId();
//
//        // Checks DB/users/{user-id}
//        fireBaseReference.child("users").child(userId).child(Long.toString(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                // If it exists, everything is sweet
//                if (snapshot.child("Status").getValue().equals("Observer")) {
//                    Log.d("firebase - event", "You're an: Observer");
//                }
//                // If it doesn't, create the user in the Firebase database
//                else if (snapshot.child("Status").getValue().equals("Driver")) {
//                    Log.d("firebase - event", "You're a: Driver");
//                } else if (snapshot.child("Status").getValue().equals("Passenger")) {
//                    Log.d("firebase - event", "You're a: Passenger");
//                } else {
//                    Log.d("firebase - event", "Did not find: " + eventId);
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//                Log.e("firebase - error", firebaseError.getMessage());
//            }
//        });


        viewPager = (ViewPager) view.findViewById(R.id.view_pager1);

        pagerAdapter = new YourPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int index) {
//                    pagerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout1);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });



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

class YourPagerAdapter extends FragmentPagerAdapter {

    public YourPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Event_Offers();
            case 1:
                return new Event_Requests();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
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
