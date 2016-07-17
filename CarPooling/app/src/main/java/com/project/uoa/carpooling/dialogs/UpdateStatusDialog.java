package com.project.uoa.carpooling.dialogs;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateStatusDialog extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private NumberPicker np;
    //EditText locationResult;
    private PlaceAutocompleteFragment locationAutoResult;

    private static final String STATUS = "param1";
    private static final String TAG = "UpdateStatusDialog";

    private Place locationSelected;
    private String status;
    private String eventID;
    private String userID;

    public UpdateStatusDialog() {
        // Required empty public constructor
    }

    public static UpdateStatusDialog newInstance(String status) {
        UpdateStatusDialog fragment = new UpdateStatusDialog();
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
        view = inflater.inflate(R.layout.fragment_status_details, container, false);

        userID = ((CarpoolEventActivity)getActivity()).getUserID();
        eventID = ((CarpoolEventActivity)getActivity()).getEventID();

        TextView statusText = (TextView) view.findViewById(R.id.status_text_popup2);

        //locationResult = (EditText) view.findViewById(R.id.locationTextResult);
        locationAutoResult = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.locationTextResult);

        locationAutoResult.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                locationSelected = place;
                Log.i(TAG, "Location selected: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred with location auto complete: " + status);
            }
        });

        TextView locText = (TextView) view.findViewById(R.id.locationText);
        TextView countText = (TextView) view.findViewById(R.id.countText);

        // Ensure Title Bar doesn't show.
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

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

                if (locationSelected != null) {
                    DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();
                    if (status.equals("Driver")) {
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Driver");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Driver");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue("True");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLat").setValue("tempLat:" + locationSelected.getLatLng().latitude);
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("StartLong").setValue("tempLong:" + locationSelected.getLatLng().longitude);
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isDriving").setValue("False");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLat").setValue("null");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("CurrentLong").setValue("null");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Passengers").child("PassengerCapacity").setValue(np.getValue());
                    } else {
                        fireBaseReference.child("users").child(userID).child("events").child(eventID).setValue("Passenger");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Status").setValue("Passenger");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("isPublic").setValue("True");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Driver").setValue("null");
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PassengerCount").setValue(np.getValue());
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLat").setValue("tempLat:" + locationSelected.getLatLng().latitude);
                        fireBaseReference.child("events").child(eventID).child("users").child(userID).child("PickupLong").setValue("tempLong:" + locationSelected.getLatLng().longitude);
                    }

                    getActivity().finish();
                    //TODO Find a way to not have to start a new Activity.
                    startActivity(getActivity().getIntent());
                }
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
