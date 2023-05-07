package com.example.foodreview;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

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
        String address = placesList.get(position).getAddress();
        holder.textViewTitle.setText(name);
        holder.textViewDesc.setText(address);
        getPhoto(placesList.get(position), holder.imageView);
        holder.cardView.setOnClickListener(view -> {
            Toast.makeText(context, "You selected " + name + "", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewDesc;
        private ImageView imageView;
        private CardView cardView;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardViewWidget);
        }


    }

    private void getPhoto(Place place, ImageView imageView) {
        PlacesClient placesClient = Places.createClient(context);

        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            Log.w(TAG, "No photo metadata.");
        } else {
            final PhotoMetadata photoMetadata = metadata.get(0);
            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);

                Log.e(TAG, "found image");
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "some error");
                    // TODO: Handle error with given status code.
                }
            });
        }
    }
}
