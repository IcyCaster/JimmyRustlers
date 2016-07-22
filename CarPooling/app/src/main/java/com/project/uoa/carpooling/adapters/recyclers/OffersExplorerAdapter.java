package com.project.uoa.carpooling.adapters.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 14/07/2016.
 */
public class OffersExplorerAdapter extends RecyclerView.Adapter<OffersExplorerViewHolder> {

    protected List<?> list = Collections.emptyList();
    private Context context;
    private String eventStatus;

    // Constructor
    public OffersExplorerAdapter(ArrayList<?> list, Context context) {
        this.list = list;
        this.context = context;
        eventStatus = ((CarpoolEventActivity)context).getEventStatus().toString();
    }

    @Override
    public void onBindViewHolder(OffersExplorerViewHolder holder, int position) {

    }

    @Override
    public OffersExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(eventStatus.equals("Driver")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__passenger_instance, parent, false);
        }
        if(eventStatus.equals("Passenger")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card__driver_instance, parent, false);
        }
        else {
            view = null;
        }
        OffersExplorerViewHolder viewHolder = new OffersExplorerViewHolder(view, context);
        return viewHolder;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

class OffersExplorerViewHolder extends RecyclerView.ViewHolder {

    public OffersExplorerViewHolder(View itemView, Context context) {
        super(itemView);



    }
}
