package com.project.uoa.carpooling.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.MainActivity;
import com.project.uoa.carpooling.entities.EventCardEntity;
import com.project.uoa.carpooling.fragments.CarPoolEventChesters;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 13/06/2016.
 */
public class SubscribedFacebookEventAdapter extends RecyclerView.Adapter<SubscribedViewHolder> {

    List<EventCardEntity> list = Collections.emptyList();
    private String testImageURL = "https://fbcdn-photos-c-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-0/c0.9.50.50/p50x50/13406798_10209109916603415_5159994816912967789_n.jpg?oh=527017c053ade13944b7dbc6db32a583&oe=58027143&__gda__=1475993774_972c6af9bda6513d8e7fceca972bd7e0";
    private Context context;

    public SubscribedFacebookEventAdapter(List<EventCardEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public SubscribedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_pool_event, parent, false);
        SubscribedViewHolder viewHolder = new SubscribedViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubscribedViewHolder holder, int position) {

        if(list.get(position).eventImageURL!=null){
//        if (!testImageURL.equals("")) {
            Picasso.with(context)
                    .load(list.get(position).eventImageURL) // should load this if it works: list.get(position).eventImageURL
                    .placeholder(R.drawable.test) // Placeholder image
                    .error(R.drawable.test) // Error image
                    // To fit image into imageView
                    .fit()
                    .noFade()
                    .into(holder.eventThumbnail);
        } else {
            // This should be the placeholderImage
            holder.eventThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.test));
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
    public String eventId;
    public ImageView eventThumbnail;
    public TextView eventName;
    public TextView eventStartDate;
    public ImageView eventStatusImage;
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private SharedPreferences sharedPreferences; // Access to SharedPreferences
    private String userId;
    private MainActivity mainActivity;

    public SubscribedViewHolder(View itemView, Context context) {
        super(itemView);
        mainActivity = (MainActivity) context;


        eventStatusImage = (ImageView) itemView.findViewById(R.id.status_photo);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("Current Facebook App-scoped ID", "");


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new CarPoolEventChesters();
                String title = eventId;

                FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contentFragment, fragment);
                ft.addToBackStack(null);
                ft.commit();

                Snackbar.make(v, "Go to " + eventId, Snackbar.LENGTH_LONG).setAction("Action", null).show();


            }
        });
    }
}


