package com.project.uoa.carpooling.adapters.recyclers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;
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
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .fit()
                    .noFade()
                    .into(holder.eventThumbnail);
        } else {
            // If no URL given, load default image
            holder.eventThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_placeholder));
        }

        // Checks DB/users/{user-id}
        DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();
        fireBaseReference.child("users").child(((MainActivity) context).getUserID()).child("events").child(list.get(position).getEventID()).addListenerForSingleValueEvent(new FirebaseValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Set the observer temp_placeholder_indicator
                if (snapshot.getValue().equals("Observer")) {
                    holder.eventStatusImage.setImageResource(R.drawable.icon_grey_circle_observer);
                }
                // Set the driver temp_placeholder_indicator
                else if (snapshot.getValue().equals("Driver")) {
                    holder.eventStatusImage.setImageResource(R.drawable.icon_grey_circle_driver);
                }
                // Set the passenger temp_placeholder_indicator
                else if (snapshot.getValue().equals("Passenger")) {
                    holder.eventStatusImage.setImageResource(R.drawable.icon_grey_circle_passenger);
                } else {
                    holder.eventStatusImage.setImageResource(R.drawable.image_placeholder);
                }

                holder.eventId = list.get(position).getEventID();
                holder.eventName.setText(list.get(position).getEventName());
                holder.eventStartDate.setText(list.get(position).getPrettyStartTime());
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



