package com.project.uoa.carpooling.adapters.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.uoa.carpooling.entities.firebase.RequestsEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Chester on 14/07/2016.
 */
public class RequestsExplorerAdapter extends RecyclerView.Adapter<RequestsExplorerViewHolder> {

    protected List<RequestsEntity> list = Collections.emptyList();
    private Context context;

    // Constructor
    public RequestsExplorerAdapter(List<RequestsEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RequestsExplorerViewHolder holder, int position) {

    }

    @Override
    public RequestsExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(0, parent, false);
        RequestsExplorerViewHolder viewHolder = new RequestsExplorerViewHolder(view, context);
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

class RequestsExplorerViewHolder extends RecyclerView.ViewHolder {

    public RequestsExplorerViewHolder(View itemView, Context context) {
        super(itemView);



    }
}
