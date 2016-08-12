package com.project.uoa.carpooling.fragments.carpool.passenger.explorer;

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
import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;
import com.project.uoa.carpooling.helpers.firebase.CarpoolMatch;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class P_E_OffersRecycler extends RecyclerView.Adapter<P_E_OffersViewHolder> {

    private List<DriverEntity> list = Collections.emptyList();
    private Context context;

    public P_E_OffersRecycler(List<DriverEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public P_E_OffersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passenger card instance
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__driver_instance, parent, false);
        // ViewHolder
        P_E_OffersViewHolder viewHolder = new P_E_OffersViewHolder(cardView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(P_E_OffersViewHolder holder, int position) {

        // Create the position specific card instance
        holder.driverID = list.get(position).getID();
        holder.driverName.setText(list.get(position).getName());

        // TODO: Calculate estimated pickup time or display something else
        holder.estimatedPickupTime.setText("TODO:");

        holder.acceptButton.setText("Approve");
        holder.declineButton.setText("Decline");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class P_E_OffersViewHolder extends RecyclerView.ViewHolder {
    protected String driverID;

    protected TextView driverName;
    protected TextView estimatedPickupTime;

    protected Button acceptButton;
    protected Button declineButton;
    protected Button mapButton;
    private DatabaseReference fireBaseReference;
    private String userID;
    private String eventID;

    public P_E_OffersViewHolder(View itemView, final Context context) {
        super(itemView);

        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;
        userID = carpoolActivity.getUserID();
        eventID = carpoolActivity.getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        driverName = (TextView) itemView.findViewById(R.id.driver_card_name);
        estimatedPickupTime = (TextView) itemView.findViewById(R.id.driver_pickuptime);

        acceptButton = (Button) itemView.findViewById(R.id.request_button);
        declineButton = (Button) itemView.findViewById(R.id.cancel_button);
        mapButton = (Button) itemView.findViewById(R.id.map_button);

        // Accept a ride from a driver
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarpoolMatch.TryJoin(eventID, driverID, userID, carpoolActivity, acceptButton, declineButton);
            }
        });

        // Decline the offer
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseReference.child("events").child(eventID).child("users").child(userID).child("Offers").child(driverID).removeValue();
                acceptButton.setEnabled(false);
                declineButton.setEnabled(false);
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
}
