package com.project.uoa.carpooling.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.enums.EventStatus;
import com.project.uoa.carpooling.helpers.firebase.CarpoolResolver;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateStatusDialog extends DialogFragment {
    private static final String STATUS = "param1";
    private static final String TAG = "UpdateStatusDialog";
    private OnFragmentInteractionListener mListener;
    private View view;
    private NumberPicker np;
    //EditText locationResult;
    private PlaceAutocompleteFragment locationAutoResult;
    private com.google.android.gms.location.places.Place locationSelected;
    private String status;
    private String eventID;
    private String userID;
    private EventStatus eventStatus;

    private double longitude = 0.0;
    private double latitude = 0.0;

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
        // Set no title bar:
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status_details, container, false);

        userID = ((CarpoolEventActivity) getActivity()).getUserID();
        eventID = ((CarpoolEventActivity) getActivity()).getEventID();
        eventStatus = ((CarpoolEventActivity) getActivity()).getEventStatus();

        TextView statusText = (TextView) view.findViewById(R.id.status_text_popup2);

        //locationResult = (EditText) view.findViewById(R.id.locationTextResult);
        locationAutoResult = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.locationTextResult);

        locationAutoResult.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
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

        if (status.equals("Driver")) {
            statusText.setText("Change To: Driver");
            locText.setText("Starting Location:");
            countText.setText("Free space in car:");
        } else {
            statusText.setText("Change To: Passenger");
            locText.setText("Pickup Location:");
            countText.setText("Total passengers:");
        }

        np = (NumberPicker) view.findViewById(R.id.numberPicker1);
        np.setMaxValue(25);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        Button confirmButton = (Button) view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (locationSelected == null) {
                    Log.d("TODO:", "Warn user they have not put a location");
                } else {


                    try {
                        longitude = locationSelected.getLatLng().longitude;
                        latitude = locationSelected.getLatLng().latitude;
                    } catch (NumberFormatException e) {
                        Log.d("TODO:", "Only accepts numbers atm");
                    }


                    if(eventStatus!=eventStatus.OBSERVER) {


                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Are you sure?");
                        alert.setMessage("Do you want to no longer be a " + eventStatus.toString() + "? " + "Everything organised will be removed and those affected will be notified!");
                        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                makeChange();

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        Dialog dialog = alert.create();
                        dialog.show();

                    }
                    else {
                        makeChange();
                    }

                }
            }
        });

        return view;
    }

    public void makeChange() {
        if (status.equals("Driver")) {
            Place startLocation = new Place("TODO: PLACE PICKER", longitude, latitude);
            CarpoolResolver.statusChange((CarpoolEventActivity) getActivity(), eventStatus.DRIVER, np.getValue(), startLocation);
        } else if (status.equals("Passenger")) {
            Place pickupLocation = new Place("TODO: PLACE PICKER", longitude, latitude);
            CarpoolResolver.statusChange((CarpoolEventActivity) getActivity(), eventStatus.PASSENGER, np.getValue(), pickupLocation);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
