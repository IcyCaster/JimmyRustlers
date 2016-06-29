package com.project.uoa.carpooling.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.entities.EventCardEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 13/06/2016.
 */
public class EventToCardAdapter extends RecyclerView.Adapter<View_Holder> {

    List<EventCardEntity> list = Collections.emptyList();
    Context context;


    // Not sure if used
    private LayoutInflater layoutInflater;
    EventToCardAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }


    public EventToCardAdapter(List<EventCardEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_pool_event, parent, false);
        View_Holder viewHolder = new View_Holder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        holder.eventName.setText(list.get(position).eventName);
        holder.eventStartDate.setText(list.get(position).startDate);
        holder.eventThumbnail.setImageResource(list.get(position).eventImageId);
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


class View_Holder extends RecyclerView.ViewHolder {
    public ImageView eventThumbnail;
    public TextView eventName;
    public TextView eventStartDate;


    public View_Holder(View itemView) {
        super(itemView);
        eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);
    }



}

