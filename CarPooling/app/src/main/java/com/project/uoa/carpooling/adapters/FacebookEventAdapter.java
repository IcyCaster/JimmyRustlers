package com.project.uoa.carpooling.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.fragments.SubscribedCarpools;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__car_pool_instance, parent, false);
        AddViewHolder viewHolder = new AddViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AddViewHolder holder, int position) {
        holder.eventId = Long.toString(list.get(position).id);
        holder.eventName.setText(list.get(position).eventName);
        holder.eventStartDate.setText(list.get(position).startDate);

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
    public ImageView eventThumbnail;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private String userId;

    public AddViewHolder(View itemView, Context context) {
        super(itemView);
        final MainActivity mainActivity = (MainActivity) context;

//        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = ((MainActivity) context).getUserId();


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Snackbar.make(v, "Subcribed to " + eventId, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                // Checks DB/events/{event-id}/users
                fireBaseReference.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it exists, everything is sweet
                        if (snapshot.exists()) {
                            Log.d("firebase - event", "Already subscribed to: " + eventId);
                        }
                        // If it doesn't, create the event in the Firebase database
                        else {

                            fireBaseReference.child("events").child(eventId).child("users").push();


                            GraphRequest request = GraphRequest.newGraphPathRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + eventId,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            try {

                                                if (response.getJSONObject().getJSONObject("place").has("name")) {
                                                    Log.d("y", "name");
                                                    fireBaseReference.child("events").child(eventId).child("location").push();
                                                    fireBaseReference.child("events").child(eventId).child("location").setValue(response.getJSONObject().getJSONObject("place").getString("name"));
                                                }
                                                if (response.getJSONObject().getJSONObject("place").has("location")) {
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("latitude")) {
                                                        Log.d("y", "lat");
                                                        fireBaseReference.child("events").child(eventId).child("latitude").push();
                                                        fireBaseReference.child("events").child(eventId).child("latitude").setValue(response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("latitude"));

                                                    }
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("longitude")) {
                                                        Log.d("y", "long");
                                                        fireBaseReference.child("events").child(eventId).child("longitude").push();
                                                        fireBaseReference.child("events").child(eventId).child("longitude").setValue(response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("longitude"));
                                                    }


                                                }

                                                SubscribedCarpools subscribedCarpools = (SubscribedCarpools)
                                                        mainActivity.getSupportFragmentManager().findFragmentById(R.id.contentFragment);
                                                subscribedCarpools.PopulateViewWithSubscribedEvents();



                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            // Insert your code here
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "place");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                        fireBaseReference.child("events").child(eventId).child("users").child(userId).child("Status").setValue("Observer");
                        fireBaseReference.child("events").child(eventId).child("users").child(userId).child("isPublic").setValue("False");

                        Log.d("firebase - event", "Subscribed to: " + eventId);

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("firebase - error", firebaseError.getMessage());
                    }
                });

                // Checks DB/users/{user-id}/events/{event-id}
                fireBaseReference.child("users").child(userId).child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it exists, everything is sweet
                        if (snapshot.exists()) {
                            Log.d("firebase - event", "Already subscribed to: " + eventId);
                        }
                        // If it doesn't, create the event in the Firebase database
                        else {
                            fireBaseReference.child("users").child(userId).child("events").child(eventId).push();
                            fireBaseReference.child("users").child(userId).child("events").child(eventId).setValue("Observer");
                            Log.d("firebase - event", "Subscribed to: " + eventId);
                        }

                        SubscribedCarpools frag = (SubscribedCarpools) mainActivity.getSupportFragmentManager().findFragmentById(R.id.contentFragment);

                        Log.d("hmm", frag.toString());


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

