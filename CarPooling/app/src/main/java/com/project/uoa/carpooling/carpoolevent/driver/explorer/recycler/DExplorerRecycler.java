package com.project.uoa.carpooling.carpoolevent.driver.explorer.recycler;

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
import com.project.uoa.carpooling.entities.facebook.SimpleFacebookEventEntity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class DExplorerRecycler extends RecyclerView.Adapter<DExplorerViewHolder> {

    private List<SimpleFacebookEventEntity> list = Collections.emptyList();
    private Context context;

    // Constructor, pass in a list of Facebook Card Events and the applications context
    public DExplorerRecycler(List<SimpleFacebookEventEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public DExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__car_pool_instance, parent, false);
        DExplorerViewHolder viewHolder = new DExplorerViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DExplorerViewHolder holder, int position) {

        holder.eventId = list.get(position).getEventID();
        holder.eventName.setText(list.get(position).getEventName());
        holder.eventStartDate.setText(list.get(position).getPrettyStartTime());


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

class DExplorerViewHolder extends RecyclerView.ViewHolder {
    protected String eventId;
    protected TextView eventName;
    protected TextView eventStartDate;
    protected ImageView eventThumbnail;
    private DatabaseReference fireBaseReference;
    private String userId;

    public DExplorerViewHolder(View itemView, Context context) {
        super(itemView);

        final MainActivity mainActivity = (MainActivity) context;

        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = mainActivity.getUserID();

        // Adds a click listener to every carpool card: the click subscribed the user to the carpool as an observer
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Checks events/{event-id} to see if the event exists in the database as this may be the first user subscribing
                fireBaseReference.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // If it does not exist it needs to be added
                        if (!snapshot.exists()) {

                            // Add a /users branch
                            fireBaseReference.child("events").child(eventId).child("users").push();

                            GraphRequest request = GraphRequest.newGraphPathRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + eventId,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            try {
                                                // If the event contains a location-name, add it.
                                                if (response.getJSONObject().has("place") && response.getJSONObject().getJSONObject("place").has("name")) {
                                                    Log.d("facebook - event", "place added: " + response.getJSONObject().getJSONObject("place").getString("name"));
                                                    fireBaseReference.child("events").child(eventId).child("location").push();
                                                    fireBaseReference.child("events").child(eventId).child("location").setValue(response.getJSONObject().getJSONObject("place").getString("name"));
                                                }
                                                if (response.getJSONObject().has("place") && response.getJSONObject().getJSONObject("place").has("location")) {
                                                    // If the event contains a location-longitude, add it.
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("latitude")) {
                                                        Log.d("facebook - event", "latitude added: " + response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("latitude"));
                                                        fireBaseReference.child("events").child(eventId).child("latitude").push();
                                                        fireBaseReference.child("events").child(eventId).child("latitude").setValue(response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("latitude"));
                                                    }
                                                    // If the event contains a location-latitude, add it.
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("longitude")) {
                                                        Log.d("facebook - event", "longitude added: " + response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("longitude"));
                                                        fireBaseReference.child("events").child(eventId).child("longitude").push();
                                                        fireBaseReference.child("events").child(eventId).child("longitude").setValue(response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("longitude"));
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            // Facebook parameters for getting the event's place
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "place");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        // Add {user-ID} to events/{event-ID}/users/ and set as an observer
                        fireBaseReference.child("events").child(eventId).child("users").child(userId).child("Status").setValue("Observer");
                        fireBaseReference.child("events").child(eventId).child("users").child(userId).child("isPublic").setValue("False");

                        // Add {event-ID} to users/{user-ID}/events/ and set as an observer
                        fireBaseReference.child("users").child(userId).child("events").child(eventId).push();
                        fireBaseReference.child("users").child(userId).child("events").child(eventId).setValue("Observer");

                        Log.d("firebase - event", "Subscribed to: " + eventId);


                        // TODO: Remove it from the list? Or acknowledge that the event has been subscribed somehow?


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
