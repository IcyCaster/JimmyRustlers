package com.project.uoa.carpooling.adapters.recyclers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 13/06/2016.
 */
public class CurrentCarpoolEventAdapter extends RecyclerView.Adapter<CurrentCarpoolEventViewHolder> {

    protected List<SimpleEventEntity> list = Collections.emptyList();
    private Context context;

    // Constructor
    public CurrentCarpoolEventAdapter(List<SimpleEventEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CurrentCarpoolEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__car_pool_instance, parent, false);
        CurrentCarpoolEventViewHolder viewHolder = new CurrentCarpoolEventViewHolder(view, context, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CurrentCarpoolEventViewHolder holder, final int position) {

        // Picasso loads image from the URL
        if (list.get(position).getEventImageURL() != null) {
            Picasso.with(context)
                    .load(list.get(position).getEventImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_no_image)
                    .fit()
                    .noFade()
                    .into(holder.eventThumbnail);
        } else {
            // If no URL given, load default image
            holder.eventThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
        }

        // Checks DB/users/{user-id}
        DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();
        fireBaseReference.child("users").child(((MainActivity) context).getUserID()).child("events").child(list.get(position).getEventID()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Set the observer temp_placeholder_indicator
                if (snapshot.getValue().equals("Observer")) {
                    holder.eventStatusImage.setImageResource(R.drawable.observer_icon);
                }
                // Set the driver temp_placeholder_indicator
                else if (snapshot.getValue().equals("Driver")) {
                    holder.eventStatusImage.setImageResource(R.drawable.driver_icon);
                }
                // Set the passenger temp_placeholder_indicator
                else if (snapshot.getValue().equals("Passenger")) {
                    holder.eventStatusImage.setImageResource(R.drawable.passenger_icon);
                } else {
                    holder.eventStatusImage.setImageResource(R.drawable.temp_placeholder_indicator);
                }

                holder.eventId = list.get(position).getEventID();
                holder.eventName.setText(list.get(position).getEventName());
                holder.eventStartDate.setText(list.get(position).getPrettyStartTime());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("firebase - error", firebaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

class CurrentCarpoolEventViewHolder extends RecyclerView.ViewHolder {
    protected String eventId;
    protected ImageView eventThumbnail;
    protected TextView eventName;
    protected TextView eventStartDate;
    protected ImageView eventStatusImage;
    private MainActivity mainActivity;

    public CurrentCarpoolEventViewHolder(final View itemView, final Context context, final CurrentCarpoolEventAdapter adapter) {
        super(itemView);
        mainActivity = (MainActivity) context;
        eventStatusImage = (ImageView) itemView.findViewById(R.id.status_photo);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventID = adapter.list.get(getAdapterPosition()).getEventID();

                // Launch the carpool instance as a new activity
                Intent i = new Intent(mainActivity, CarpoolEventActivity.class);
                Bundle b = new Bundle();
                b.putString("userID", mainActivity.getUserID());
                b.putString("eventID", eventID);
                i.putExtras(b);
                mainActivity.startActivity(i);
            }


        });
    }
}



