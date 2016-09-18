package com.project.uoa.carpooling.adapters.recyclers;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;
import com.project.uoa.carpooling.entities.shared.Place;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

/**
 * ExploreCarpoolEventAdapter is created to display SimpleEvents, from Facebook, for the users so they can join. Once clicked they will be added to the user's subscription list.
 * <p/>
 * * Created by Angel and Chester on 13/06/2016.
 */
public class ExploreCarpoolEventAdapter extends RecyclerView.Adapter<ExploreCarpoolEventViewHolder> {

    private List<SimpleEventEntity> list = Collections.emptyList();
    private Context context;

    // Constructor, pass in a list of Facebook Card Events and the applications context
    public ExploreCarpoolEventAdapter(List<SimpleEventEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ExploreCarpoolEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__car_pool_instance, parent, false);
        ExploreCarpoolEventViewHolder viewHolder = new ExploreCarpoolEventViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExploreCarpoolEventViewHolder holder, int position) {

        holder.eventID = list.get(position).getEventID();
        holder.eventName.setText(list.get(position).getEventName());
        holder.eventStartDate.setText(list.get(position).getPrettyStartTime());

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

// ViewHolder is needed to inflate multiple CardViews on the fly for displaying in the RecyclerView
class ExploreCarpoolEventViewHolder extends RecyclerView.ViewHolder {
    protected String eventID;
    protected TextView eventName;
    protected TextView eventStartDate;
    protected ImageView eventThumbnail;
    private DatabaseReference fireBaseReference;
    private String userId;

    public ExploreCarpoolEventViewHolder(View itemView, final Context context) {
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
                fireBaseReference.addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        // If it does not exist it needs to be added
                        if (!snapshot.child("events").child(eventID).exists()) {

                            // Add a /users branch
                            fireBaseReference.child("events").child(eventID).child("users").push();

                            GraphRequest request = GraphRequest.newGraphPathRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + eventID,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            try {

                                                String placeName = null;
                                                double latitude = 0.0;
                                                double longitude = 0.0;

                                                // If the event contains a location-name, add it.
                                                if (response.getJSONObject().has("place") && response.getJSONObject().getJSONObject("place").has("name")) {
                                                    placeName = response.getJSONObject().getJSONObject("place").getString("name");
                                                    Log.d("facebook - event", "place added: " + placeName);
                                                }
                                                if (response.getJSONObject().has("place") && response.getJSONObject().getJSONObject("place").has("location")) {
                                                    // If the event contains a location-longitude, add it.
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("latitude")) {
                                                        String lat = response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("latitude");
                                                        latitude = Double.parseDouble(lat);
                                                        Log.d("facebook - event", "latitude added: " + lat);
                                                    }
                                                    // If the event contains a location-latitude, add it.
                                                    if (response.getJSONObject().getJSONObject("place").getJSONObject("location").has("longitude")) {
                                                        String lng = response.getJSONObject().getJSONObject("place").getJSONObject("location").getString("longitude");
                                                        Log.d("facebook - event", "longitude added: " + lng);
                                                        longitude = Double.parseDouble(lng);
                                                    }
                                                }

                                                Place destinationLocation = new Place(placeName, latitude, longitude);
                                                fireBaseReference.child("events").child(eventID).child("destination").setValue(destinationLocation);

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

                        // Add {user-ID} to events/{event-ID}/users/, set as an observer and add users name
                        fireBaseReference.child("events").child(eventID).child("users").child(userId).child("Status").setValue("Observer");
                        fireBaseReference.child("events").child(eventID).child("users").child(userId).child("isPublic").setValue(false);
                        fireBaseReference.child("events").child(eventID).child("users").child(userId).child("Name").setValue(snapshot.child("users").child(userId).child("Name").getValue());

                        // Add {event-ID} to users/{user-ID}/events/ and set as an observer
                        fireBaseReference.child("users").child(userId).child("events").child(eventID).push();
                        fireBaseReference.child("users").child(userId).child("events").child(eventID).setValue("Observer");

                        // TODO Future explorations to provide feedback to the user. Usability studies indicated that they did not know if they had joined a carpool or not.
                    }
                });
            }
        });
    }
}

