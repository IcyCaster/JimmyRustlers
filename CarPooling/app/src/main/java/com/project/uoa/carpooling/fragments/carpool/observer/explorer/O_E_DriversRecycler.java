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
import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class O_E_DriversRecycler extends RecyclerView.Adapter<O_E_DriversViewHolder> {

    private List<DriverEntity> list = Collections.emptyList();
    private Context context;

    public O_E_DriversRecycler(List<DriverEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public O_E_DriversViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Driver card instance
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__driver_instance, parent, false);
        // ViewHolder
        O_E_DriversViewHolder viewHolder = new O_E_DriversViewHolder(cardView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(O_E_DriversViewHolder holder, int position) {

        holder.driverID = list.get(position).getID();
        holder.driverName.setText(list.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class O_E_DriversViewHolder extends RecyclerView.ViewHolder {
    protected String driverID;

    protected TextView driverName;

    protected Button mapButton;

    private DatabaseReference fireBaseReference;
    private String userID;
    private String eventID;

    public O_E_DriversViewHolder(View itemView, Context context) {
        super(itemView);

        final CarpoolEventActivity carpoolActivity = (CarpoolEventActivity) context;
        userID = carpoolActivity.getUserID();
        eventID = carpoolActivity.getEventID();

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        driverName = (TextView) itemView.findViewById(R.id.driver_card_name);

        mapButton = (Button) itemView.findViewById(R.id.map_button);

        // Shows observers's location respective to driver and their route
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: OPEN UP MAP, SHOW WHAT THE NEW ROUTE WILL LOOK LIKE WITH THE NEW PASSENGER + TOTAL TIME + TOTAL DISTANCE

            }
        });


    }
}
