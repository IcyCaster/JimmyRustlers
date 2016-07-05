package com.project.uoa.carpooling.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_Carpool.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_Carpool#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Carpool extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String EVENT_ID = "param1";

    // TODO: Rename and change types of parameters
    private Long eventId;

    private OnFragmentInteractionListener mListener;

    public Event_Carpool() {
        // Required empty public constructor
    }

    public static Event_Carpool newInstance(Long eventId) {
        Event_Carpool fragment = new Event_Carpool();

        Log.d("EventId2", Long.toString(eventId));

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
        return inflater.inflate(R.layout.fragment_event__carpool, container, false);
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
