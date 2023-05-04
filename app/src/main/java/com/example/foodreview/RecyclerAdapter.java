package com.example.foodreview;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PlacesViewHolder> {
    List<Place> placesList;
    private Context context;

    public RecyclerAdapter(List<Place> placesList, Context context) {
        this.placesList = placesList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);

        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        String name = placesList.get(position).getName();
        holder.textViewTitle.setText(name);

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewDesc;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(TAG, "hi");

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
        }


    }
}
