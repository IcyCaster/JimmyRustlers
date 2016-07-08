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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_pool_event, parent, false);
        SubscribedViewHolder viewHolder = new SubscribedViewHolder(view, context, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubscribedViewHolder holder, int position) {

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

        holder.eventId = Long.toString(list.get(position).id);
        holder.eventName.setText(list.get(position).eventName);
        holder.eventStartDate.setText(list.get(position).startDate);
        holder.eventStatusImage.setImageResource(R.drawable.indicator);
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

                final long l = eventAdapter.list.get(getAdapterPosition()).id;


                Log.d("Event id:", Long.toString(l));

//                Fragment fragment = CarpoolEvent.newInstance(l);
//
//                FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.contentFragment, fragment);
//                ft.addToBackStack(null);
//                ft.commit();


                // Checks DB/users/{user-id}
                fireBaseReference.child("users").child(mainActivity.getUserId()).child(Long.toString(l)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it exists, everything is sweet
                        if (snapshot.child("Status").getValue().equals("Observer")) {
                            Log.d("firebase - event", "You're an: Observer");
                            status = "Observer";
                        }
                        // If it doesn't, create the user in the Firebase database
                        else if (snapshot.child("Status").getValue().equals("Driver")) {
                            Log.d("firebase - event", "You're a: Driver");
                            status = "Driver";
                        } else if (snapshot.child("Status").getValue().equals("Passenger")) {
                            Log.d("firebase - event", "You're a: Passenger");
                            status = "Passenger";
                        } else {
                            Log.d("firebase - event", "Did not find it ");
                            status = "Error";
                        }

                        Intent i = new Intent(mainActivity, CarpoolEventActivity.class);

                        Bundle b = new Bundle();
                        b.putString("userID", mainActivity.getUserId());
                        b.putLong("eventID", l);
                        b.putString("eventStatus", status);
                        i.putExtras(b);
                        mainActivity.startActivity(i);

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("firebase - error", firebaseError.getMessage());
                    }
                });





//                Snackbar.make(v, "Go to " + eventId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }


        });
    }


}



