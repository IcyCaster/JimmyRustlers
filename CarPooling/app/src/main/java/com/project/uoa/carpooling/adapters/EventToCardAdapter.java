package com.project.uoa.carpooling.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.uoa.carpooling.R;

/**
 * Created by Chester on 13/06/2016.
 */
public class EventToCardAdapter extends RecyclerView.Adapter {

    static class EventToCard extends RecyclerView.ViewHolder {
        private ImageView eventThumbnail;
        private TextView eventName;
        private TextView eventStartDate;


        public EventToCard(View itemView) {
            super(itemView);
            eventThumbnail = (ImageView) itemView.findViewById(R.id.event_photo);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventStartDate = (TextView) itemView.findViewById(R.id.event_start_date);
        }



    }





    private LayoutInflater layoutInflater;

    EventToCardAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public EventToCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_car_pool_event, parent, false);
        EventToCard viewHolder = new EventToCard(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        //TODO https://www.youtube.com/watch?list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD&v=I2eYBtLWGzc
        //7:31

        //..... before i can do this i need to make a JSON parser for carpool events.



    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        public EventViewHolder(View itemView) {
            super(itemView);
        }
    }


}
