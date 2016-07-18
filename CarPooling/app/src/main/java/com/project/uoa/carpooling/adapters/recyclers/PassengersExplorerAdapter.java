package com.project.uoa.carpooling.adapters.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.entities.firebase.PassengersEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 14/07/2016.
 */
public class PassengersExplorerAdapter extends RecyclerView.Adapter<PassengersExplorerViewHolder> {

    protected List<PassengersEntity> list = Collections.emptyList();
    private Context context;

    // Constructor
    public PassengersExplorerAdapter(List<PassengersEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(PassengersExplorerViewHolder holder, int position) {

    }

    @Override
    public PassengersExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(0, parent, false);
        PassengersExplorerViewHolder viewHolder = new PassengersExplorerViewHolder(view, context);
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

class PassengersExplorerViewHolder extends RecyclerView.ViewHolder {

    public PassengersExplorerViewHolder(View itemView, Context context) {
        super(itemView);



    }
}
