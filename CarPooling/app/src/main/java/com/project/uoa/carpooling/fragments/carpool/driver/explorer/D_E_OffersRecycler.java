package com.project.uoa.carpooling.fragments.carpool.driver.explorer;

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
import com.project.uoa.carpooling.fragments.carpool._entities.PassengerEntity;

import java.util.Collections;
import java.util.List;

/**
 * D_E_OffersRecycler is created to display Passengers for the driver so that could offer a ride to them.
 *
 * * Created by Angel and Chester
 */
public class D_E_OffersRecycler extends RecyclerView.Adapter<D_E_OffersViewHolder> {

    private List<PassengerEntity> list = Collections.emptyList();
    private Context context;

    public D_E_OffersRecycler(List<PassengerEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public D_E_OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passenger card instance
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__passenger_instance, parent, false);
        // ViewHolder
        D_E_OffersViewHolder viewHolder = new D_E_OffersViewHolder(cardView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(D_E_OffersViewHolder holder, int position) {

        // Create the position specific card instance
        holder.passengerID = list.get(position).getID();
        holder.passengerName.setText(list.get(position).getName());
        holder.passengerLocation.setText("Location: " + list.get(position).getPickupLocation().toString());
        holder.passengerCount.setText("Passenger Count: " + Integer.toString(list.get(position).getPassengerCount()));

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

class D_E_OffersViewHolder extends RecyclerView.ViewHolder {
    protected String passengerID;
    protected TextView passengerName;
    protected TextView passengerLocation;
    protected TextView passengerCount;
    protected Button offerButton;
    protected Button cancelButton;
    protected Button mapButton;
    private DatabaseReference fireBaseReference;
    private String userID;
    private String eventID;

    public D_E_OffersViewHolder(View itemView, Context context) {
        super(itemView);

        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;
        userID = carpoolActivity.getUserID();
        eventID = carpoolActivity.getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        passengerName = (TextView) itemView.findViewById(R.id.passenger_name);
        passengerLocation = (TextView) itemView.findViewById(R.id.passenger_location);
        passengerCount = (TextView) itemView.findViewById(R.id.passenger_count);
        offerButton = (Button) itemView.findViewById(R.id.request_button);
        cancelButton = (Button) itemView.findViewById(R.id.cancel_button);
        mapButton = (Button) itemView.findViewById(R.id.map_button);

        // Offers a ride to a passenger
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseReference.child("events").child(eventID).child("users").child(passengerID).child("Offers").child(userID).setValue("Pending");
                pendingOffer();
            }
        });

        // Cancels the offer
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseReference.child("events").child(eventID).child("users").child(passengerID).child("Offers").child(userID).removeValue();
                blankOffer();
            }
        });

        // Shows passenger's location respective to driver and their route
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Future work for showing a map of what it would look like if this passenger was added to their route
            }
        });
    }

    // Shows cancel button
    public void pendingOffer() {
        cancelButton.setVisibility(View.VISIBLE);
        offerButton.setText("Pending");
        offerButton.setEnabled(false);
    }

    // Hides cancel button
    public void blankOffer() {
        cancelButton.setVisibility(View.GONE);
        offerButton.setText("Offer");
        offerButton.setEnabled(true);
    }
}
