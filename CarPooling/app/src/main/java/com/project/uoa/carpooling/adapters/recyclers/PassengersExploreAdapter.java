package com.project.uoa.carpooling.adapters.recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Chester on 14/07/2016.
 */
public class PassengersExploreAdapter extends RecyclerView.Adapter<ExploreCarpoolEventViewHolder>{
    @Override
    public ExploreCarpoolEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ExploreCarpoolEventViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

class PassengersExplorerViewHolder extends RecyclerView.ViewHolder {

    public PassengersExplorerViewHolder(View itemView) {
        super(itemView);
    }
}
