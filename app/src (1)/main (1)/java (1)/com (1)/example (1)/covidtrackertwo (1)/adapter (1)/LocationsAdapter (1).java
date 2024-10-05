package com.example.covidtrackertwo.adapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;


import com.example.covidtrackertwo.R;
import com.example.covidtrackertwo.data.Location;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private ArrayList<Location> data;
    private OnItemClickListener onItemClickListener;

    private NavController navController;

    public LocationsAdapter(ArrayList<Location> data, NavController navController, OnItemClickListener listener) {
        this.data = data;
        this.onItemClickListener = listener;
        this.navController = navController;
    }

    @NonNull
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsAdapter.ViewHolder holder, int position) {

        // This is where you update each view on the list
        Location location = data.get(position);
        holder.locationName.setText(location.getLocationName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pass the argument value to the click listener
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(location.getLocationName());
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(String location_name_arg);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationName;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locationName = itemView.findViewById(R.id.location_name_textview);
            cardView = itemView.findViewById(R.id.location_item_cardview);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Location location = data.get(position);
                        String argumentValue = location.getLocationName();

                        Bundle args = new Bundle();
                        args.putString("location_name", argumentValue);
                        Log.i("LocationsAdapter", argumentValue);
                        navController.navigate(R.id.action_locationsFragment_to_viewLocationFragment, args);
                    }
                }
            });

        }
    }

}
