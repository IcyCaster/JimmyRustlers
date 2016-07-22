package com.project.uoa.carpooling.carpoolevent.driver.explorer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.firebase.PassengersEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class DriverExplorerRecycler extends RecyclerView.Adapter<DriverExplorerViewHolder> {

    private List<PassengerEntity> list = Collections.emptyList();
    private Context context;

    // Constructor, pass in a list of Facebook Card Events and the applications context
    public DriverExplorerRecycler(List<PassengerEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public DriverExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__passenger_instance, parent, false);
        DriverExplorerViewHolder viewHolder = new DriverExplorerViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DriverExplorerViewHolder holder, int position) {

        holder.passengerName.setText(list.get(position).getName());
        holder.passengerLocation.setText("Location: " + list.get(position).getPickupLocation().toString());
        holder.passengerCount.setText("Passenger Count: " + Integer.toString(list.get(position).getPassengerCount()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }
}

class DriverExplorerViewHolder extends RecyclerView.ViewHolder {
    protected TextView passengerName;
    protected TextView passengerLocation;
    protected TextView passengerCount;
    private DatabaseReference fireBaseReference;
    private String userId;

    public DriverExplorerViewHolder(View itemView, Context context) {
        super(itemView);
        passengerName = (TextView) itemView.findViewById(R.id.passenger_name);
        passengerLocation = (TextView) itemView.findViewById(R.id.passenger_location);
        passengerCount = (TextView) itemView.findViewById(R.id.passenger_count);


        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = carpoolActivity.getUserID();

    }
}
