package com.project.uoa.carpooling.carpoolevent.passenger.explorer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.carpoolevent._entities.DriverEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_E_RequestsRecycler extends RecyclerView.Adapter<P_E_RequestsViewHolder> {

    private List<DriverEntity> list = Collections.emptyList();
    private Context context;

    public P_E_RequestsRecycler(List<DriverEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public P_E_RequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Driver card instance
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__driver_instance, parent, false);
        // ViewHolder
        P_E_RequestsViewHolder viewHolder = new P_E_RequestsViewHolder(cardView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(P_E_RequestsViewHolder holder, int position) {

        holder.driverID = list.get(position).getID();
        holder.driverName.setText(list.get(position).getName());
        holder.estimatedPickupTime.setText("TODO");

        if (list.get(position).isPending()) {
            holder.pendingOffer();
        } else {
            holder.blankOffer();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class P_E_RequestsViewHolder extends RecyclerView.ViewHolder {
    protected String driverID;

    protected TextView driverName;
    protected TextView estimatedPickupTime;

    protected Button requestButton;
    protected Button cancelButton;
    protected Button mapButton;
    private DatabaseReference fireBaseReference;
    private String userID;
    private String eventID;

    public P_E_RequestsViewHolder(View itemView, Context context) {
        super(itemView);

        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;
        userID = carpoolActivity.getUserID();
        eventID = carpoolActivity.getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        driverName = (TextView) itemView.findViewById(R.id.driver_card_name);
        estimatedPickupTime = (TextView) itemView.findViewById(R.id.driver_pickuptime);

        requestButton = (Button) itemView.findViewById(R.id.request_button);
        cancelButton = (Button) itemView.findViewById(R.id.cancel_button);
        mapButton = (Button) itemView.findViewById(R.id.map_button);

        // Requests a driver to give a ride
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("Requests").child(userID).setValue("Pending");
                pendingOffer();
            }
        });

        // Cancels the request
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: ADD A POPUP TO CONFIRM CANCELLING "THE OFFER"
                fireBaseReference.child("events").child(eventID).child("users").child(driverID).child("Requests").child(userID).removeValue();
                blankOffer();

            }
        });

        // Shows passenger's location respective to driver and their route
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: OPEN UP MAP, SHOW WHAT THE NEW ROUTE WILL LOOK LIKE WITH THE NEW PASSENGER + TOTAL TIME + TOTAL DISTANCE

            }
        });


    }

    // Shows cancel button
    public void pendingOffer() {
        cancelButton.setVisibility(View.VISIBLE);
        requestButton.setText("Pending");
        requestButton.setEnabled(false);
    }

    // Hides cancel button
    public void blankOffer() {
        cancelButton.setVisibility(View.GONE);
        requestButton.setText("Request");
        requestButton.setEnabled(true);
    }
}
