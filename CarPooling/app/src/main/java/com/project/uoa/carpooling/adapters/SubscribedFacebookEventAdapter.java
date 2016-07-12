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

    List<EventCardEntity> list = Collections.emptyList();
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

        final DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // This is used to load the image
        if (list.get(position).eventImageURL != null) {
            Picasso.with(context)
                    .load(list.get(position).eventImageURL) // should load this if it works: list.get(position).eventImageURL
                    .placeholder(R.drawable.placeholder_image) // Placeholder image
                    .error(R.drawable.error_no_image) // Error image
                    .fit()
                    .noFade()
                    .into(holder.eventThumbnail);
        } else {
            holder.eventThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
        }

        // Checks DB/users/{user-id}
        fireBaseReference.child("users").child(((MainActivity)context).getUserId()).child("events").child(Long.toString(list.get(position).id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // If it exists, everything is sweet
                if (snapshot.getValue().equals("Observer")) {
                    Log.d("firebase - event", "You're an: Observer");
                    holder.eventStatusImage.setImageResource(R.drawable.observer_icon);
                }
                // If it doesn't, create the user in the Firebase database
                else if (snapshot.getValue().equals("Driver")) {
                    Log.d("firebase - event", "You're a: Driver");
                    holder.eventStatusImage.setImageResource(R.drawable.driver_icon);
                } else if (snapshot.getValue().equals("Passenger")) {
                    Log.d("firebase - event", "You're a: Passenger");
                    holder.eventStatusImage.setImageResource(R.drawable.passenger_icon);
                } else {
                    Log.d("firebase - event", "Did not find it ");
                    holder.eventStatusImage.setImageResource(R.drawable.indicator);

                }

                holder.eventId = Long.toString(list.get(position).id);
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

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, EventCardEntity data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(EventCardEntity data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

}

class SubscribedViewHolder extends RecyclerView.ViewHolder {
    protected String eventId;
    protected ImageView eventThumbnail;
    protected TextView eventName;
    protected TextView eventStartDate;
    protected ImageView eventStatusImage;
    private MainActivity mainActivity;
    String status = "NOTHING";


    public SubscribedViewHolder(final View itemView, final Context context, SubscribedFacebookEventAdapter adapter) {
        super(itemView);
        final SubscribedFacebookEventAdapter eventAdapter = adapter;
        mainActivity = (MainActivity) context;
        eventStatusImage = (ImageView) itemView.findViewById(R.id.status_photo);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);


        final DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String eventID = Long.toString(eventAdapter.list.get(getAdapterPosition()).id);


                Log.d("Event id:", eventID);


                // Checks DB/users/{user-id}
                fireBaseReference.child("users").child(mainActivity.getUserId()).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it exists, everything is sweet
                        if (snapshot.getValue().equals("Observer")) {
                            Log.d("firebase - event", "You're an: Observer");
                            status = "Observer";
                        }
                        // If it doesn't, create the user in the Firebase database
                        else if (snapshot.getValue().equals("Driver")) {
                            Log.d("firebase - event", "You're a: Driver");
                            status = "Driver";
                        } else if (snapshot.getValue().equals("Passenger")) {
                            Log.d("firebase - event", "You're a: Passenger");
                            status = "Passenger";
                        } else {
                            Log.d("firebase - event", "Did not find it ");
                            status = "Error";
                        }

                        Intent i = new Intent(mainActivity, CarpoolEventActivity.class);

                        Bundle b = new Bundle();
                        b.putString("userID", mainActivity.getUserId());
                        b.putString("eventID", eventID);
                        b.putString("eventStatus", status);
                        i.putExtras(b);
                        mainActivity.startActivity(i);

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("firebase - error", firebaseError.getMessage());
                    }
                });

            }


        });
    }


}



