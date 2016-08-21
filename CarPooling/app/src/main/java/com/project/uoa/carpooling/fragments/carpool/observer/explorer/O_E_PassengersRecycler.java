package com.project.uoa.carpooling.fragments.carpool.observer.explorer;

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
 * Created by Chester on 18/07/2016.
 */
public class O_E_PassengersRecycler extends RecyclerView.Adapter<O_E_PassengersViewHolder> {

    private List<PassengerEntity> list = Collections.emptyList();
    private Context context;

    public O_E_PassengersRecycler(List<PassengerEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public O_E_PassengersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passenger card instance
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__passenger_instance, parent, false);
        // ViewHolder
        O_E_PassengersViewHolder viewHolder = new O_E_PassengersViewHolder(cardView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(O_E_PassengersViewHolder holder, int position) {

        // Create the position specific card instance
        holder.passengerID = list.get(position).getID();
        holder.passengerName.setText(list.get(position).getName());
        holder.passengerLocation.setText("Location: " + list.get(position).getPickupLocation().toString());
        holder.passengerCount.setText("Passenger Count: " + Integer.toString(list.get(position).getPassengerCount()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class O_E_PassengersViewHolder extends RecyclerView.ViewHolder {
    protected String passengerID;
    protected TextView passengerName;
    protected TextView passengerLocation;
    protected TextView passengerCount;
    protected Button mapButton;
    private DatabaseReference fireBaseReference;
    private String userID;
    private String eventID;

    public O_E_PassengersViewHolder(View itemView, Context context) {
        super(itemView);

        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;
        userID = carpoolActivity.getUserID();
        eventID = carpoolActivity.getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        passengerName = (TextView) itemView.findViewById(R.id.passenger_name);
        passengerLocation = (TextView) itemView.findViewById(R.id.passenger_location);
        passengerCount = (TextView) itemView.findViewById(R.id.passenger_count);


        // These buttons are not used on the reused passenger cards
        Button offerButton = (Button) itemView.findViewById(R.id.request_button);
        Button cancelButton = (Button) itemView.findViewById(R.id.cancel_button);
        offerButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        mapButton = (Button) itemView.findViewById(R.id.map_button);
        mapButton.setText("Observe on map");
        // Shows passenger's location respective to driver and their route
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: OPEN UP MAP, SHOW WHAT THE NEW ROUTE WILL LOOK LIKE WITH THE NEW PASSENGER + TOTAL TIME + TOTAL DISTANCE

            }
        });


    }
}
