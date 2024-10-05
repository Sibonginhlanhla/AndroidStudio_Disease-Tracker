package com.example.covidtrackertwo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidtrackertwo.R;
import com.example.covidtrackertwo.data.History;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> data;


    public HistoryAdapter(ArrayList<History> data) {
        this.data = data;
    }

    //public HistoryAdapter(ArrayList<History> itemList, NavController navController, OnItemClickListener location_id) {}


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        /// This is where you update each view on the list
        History history = data.get(position);
        holder.locationName.setText(history.getLocationName());
        holder.dateTextview.setText(history.getDate());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView locationName;
        TextView dateTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_textview);
            dateTextview = itemView.findViewById(R.id.date_textview);
            cardView = itemView.findViewById(R.id.history_item_cardview);
        }
    }

}
