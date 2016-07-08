package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.adapters.CarpoolEventPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarpoolEvent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarpoolEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarpoolEvent extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EVENT_ID = "param1";

    private Long eventId;
    private ViewPager viewPager;
    private CarpoolEventPagerAdapter pagerAdapter;
    private View view;
    private String status;

    //Firebase things
    private DatabaseReference fireBaseReference;

    private String userId;


    private OnFragmentInteractionListener mListener;

    public CarpoolEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CarpoolEvent newInstance(Long eventId) {
        CarpoolEvent fragment = new CarpoolEvent();
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_car_pool_event_chesters, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        fireBaseReference = FirebaseDatabase.getInstance().getReference();
        userId = ((MainActivity) getActivity()).getUserId();


        pagerAdapter = new CarpoolEventPagerAdapter(getChildFragmentManager(), eventId, (MainActivity) getActivity());



//        Log.d("EventId1", Long.toString(eventId));


        viewPager.setAdapter(pagerAdapter);
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//                @Override
//                public void onPageSelected(int index) {
//                        pagerAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onPageScrolled(int arg0, float arg1, int arg2) {
//                    // TODO Auto-generated method stub
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int arg0) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getStatus() {
        return pagerAdapter.getStatus();
    }

    public void fetchStatus() {


        // Checks DB/users/{user-id}
        fireBaseReference.child("users").child(userId).child(Long.toString(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // If it exists, everything is sweet
                if (snapshot.child("Status").getValue().equals("Observer")) {
                    Log.d("firebase - event", "You're an: Observer");
                    status = "O";
                }
                // If it doesn't, create the user in the Firebase database
                else if (snapshot.child("Status").getValue().equals("Driver")) {
                    Log.d("firebase - event", "You're a: Driver");
                    status = "D";
                } else if (snapshot.child("Status").getValue().equals("Passenger")) {
                    Log.d("firebase - event", "You're a: Passenger");
                    status = "P";
                } else {
                    Log.d("firebase - event", "Did not find it ");
                    status = "P";
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });
    }
}
