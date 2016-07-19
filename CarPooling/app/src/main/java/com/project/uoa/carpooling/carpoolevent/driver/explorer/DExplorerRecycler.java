package com.project.uoa.carpooling.carpoolevent.driver.explorer;

import android.content.Context;
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

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 18/07/2016.
 */
public class DExplorerRecycler extends RecyclerView.Adapter<DExplorerViewHolder> {

    private List<?> list = Collections.emptyList();
    private Context context;

    // Constructor, pass in a list of Facebook Card Events and the applications context
    public DExplorerRecycler(List<?> list, Context context) {
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

//        holder.eventId = list.get(position).getEventID();
//        holder.eventName.setText(list.get(position).getEventName());
//        holder.eventStartDate.setText(list.get(position).getPrettyStartTime());
//
//
//        // Picasso loads image from the URL
//        if (list.get(position).getEventImageURL() != null) {
//            Picasso.with(context)
//                    .load(list.get(position).getEventImageURL())
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.error_no_image)
//                    .fit()
//                    .noFade()
//                    .into(holder.eventThumbnail);
//        } else {
//            // If no URL given, load default image
//            holder.eventThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_image));
//        }
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

        // Connect to Firebase
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialise shared preferences
        userId = mainActivity.getUserID();

        // Adds a click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
}
