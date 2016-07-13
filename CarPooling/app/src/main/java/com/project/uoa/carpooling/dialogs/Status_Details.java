package com.project.uoa.carpooling.dialogs;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class Status_Details extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    View view;
NumberPicker np;
    EditText locationResult;


    private static final String STATUS = "param1";



    private String status;
private String eventID;
    private String userID;

    public Status_Details() {
        // Required empty public constructor
    }

    public static Status_Details newInstance(String status) {
        Status_Details fragment = new Status_Details();
        Bundle args = new Bundle();
        args.putString(STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__status_popup_page_2, container, false);

        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();

        TextView statusText = (TextView) view.findViewById(R.id.status_text_popup2);

        locationResult = (EditText) view.findViewById(R.id.locationTextResult);

        TextView locText = (TextView) view.findViewById(R.id.locationText);
        TextView countText = (TextView) view.findViewById(R.id.countText);

        if(status.equals("Driver")) {
            statusText.setText("Change To: Driver");
            locText.setText("Starting Location:");
            countText.setText("Free space in car:");
        }
        else {
            statusText.setText("Change To: Passenger");
            locText.setText("Pickup Location:");
            countText.setText("Total passengers:");
        }

        np = (NumberPicker) view.findViewById(R.id.numberPicker1);
        np.setMaxValue(25);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);

        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });




        Button confirmButton = (Button)view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();

                if(status.equals("Driver")) {
                    fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Driver");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Driver");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue("True");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLat").setValue("tempLat:" + locationResult.getText());
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLong").setValue("tempLong:" + locationResult.getText());
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").setValue("False");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLat").setValue("null");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLong").setValue("null");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Passengers").child("PassengerCapacity").setValue(np.getValue());
                }
                else {
                    fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Passenger");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Passenger");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue("True");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Driver").setValue("null");
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PassengerCount").setValue(np.getValue());
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLat").setValue("tempLat:" + locationResult.getText());
                    fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLong").setValue("tempLong:" + locationResult.getText());
                }

                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });



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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
