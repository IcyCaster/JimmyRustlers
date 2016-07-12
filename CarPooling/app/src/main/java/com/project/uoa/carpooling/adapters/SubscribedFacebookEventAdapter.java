package com.project.uoa.carpooling.adapters;

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
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 13/06/2016.
 */
public class SubscribedFacebookEventAdapter extends RecyclerView.Adapter<SubscribedViewHolder> {

    protected List<EventCardEntity> list = Collections.emptyList();
    private Context context;

    // Constructor
    public SubscribedFacebookEventAdapter(List<EventCardEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public SubscribedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__car_pool_instance, parent, false);
        SubscribedViewHolder viewHolder = new SubscribedViewHolder(view, context, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SubscribedViewHolder holder, final int position) {

        // Picasso loads image from the URL
        if (list.get(position).eventImageURL != null) {
            Picasso.with(context)
                    .load(list.get(position).eventImageURL)
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
        fireBaseReference.child("users").child(((MainActivity) context).getUserID()).child("events").child(list.get(position).eventID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Set the observer indicator
                if (snapshot.getValue().equals("Observer")) {
                    holder.eventStatusImage.setImageResource(R.drawable.observer_icon);
                }
                // Set the driver indicator
                else if (snapshot.getValue().equals("Driver")) {
                    holder.eventStatusImage.setImageResource(R.drawable.driver_icon);
                }
                // Set the passenger indicator
                else if (snapshot.getValue().equals("Passenger")) {
                    holder.eventStatusImage.setImageResource(R.drawable.passenger_icon);
                } else {
                    holder.eventStatusImage.setImageResource(R.drawable.indicator);
                }

                holder.eventId = list.get(position).eventID;
                holder.eventName.setText(list.get(position).eventName);
                holder.eventStartDate.setText(list.get(position).startDate);
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

class SubscribedViewHolder extends RecyclerView.ViewHolder {
    protected String eventId;
    protected ImageView eventThumbnail;
    protected TextView eventName;
    protected TextView eventStartDate;
    protected ImageView eventStatusImage;
    private MainActivity mainActivity;

    public SubscribedViewHolder(final View itemView, final Context context, final SubscribedFacebookEventAdapter adapter) {
        super(itemView);
        mainActivity = (MainActivity) context;
        eventStatusImage = (ImageView) itemView.findViewById(R.id.status_photo);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventID = adapter.list.get(getAdapterPosition()).eventID;

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



