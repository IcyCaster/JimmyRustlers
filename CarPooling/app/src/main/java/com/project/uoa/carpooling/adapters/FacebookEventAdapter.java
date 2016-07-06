package com.project.uoa.carpooling.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.EventCardEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 13/06/2016.
 */
public class FacebookEventAdapter extends RecyclerView.Adapter<AddViewHolder> {

    private List<EventCardEntity> list = Collections.emptyList();
    private Context context;

    // Constructor, pass in a list of Facebook Card Events and the applications context
    public FacebookEventAdapter(List<EventCardEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public AddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_pool_event, parent, false);
        AddViewHolder viewHolder = new AddViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AddViewHolder holder, int position) {
        holder.eventId = Long.toString(list.get(position).id);
        holder.eventName.setText(list.get(position).eventName);
        holder.eventStartDate.setText(list.get(position).startDate);
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


class AddViewHolder extends RecyclerView.ViewHolder {
    public String eventId;
    public TextView eventName;
    public TextView eventStartDate;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;

    public AddViewHolder(View itemView, Context context) {
        super(itemView);
        final MainActivity mainActivity = (MainActivity)context;

//        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = ((MainActivity) context).getUserId();


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

//                Snackbar.make(v, eventId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Snackbar.make(v, "Subcribed to " + eventId, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                // Checks DB/users/{user-id}
                fireBaseReference.child("users").child(userId).child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it exists, everything is sweet
                        if (snapshot.exists()) {
                            Log.d("firebase - event", "Already subscribed to: " + eventId);
                        }
                        // If it doesn't, create the user in the Firebase database
                        else {
                            fireBaseReference.child("users").child(userId).child(eventId).push();
                            fireBaseReference.child("users").child(userId).child(eventId).child("Status").setValue("Observer");
                            fireBaseReference.child("users").child(userId).child(eventId).child("isPublic").setValue("False");
                            Log.d("firebase - event", "Subscribed to: " + eventId);
                        }
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

