package com.example.foodreview;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodreview.models.ReviewData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewsViewHolder> {

    List<ReviewData> data;
    private Context context;

    public ReviewAdapter(List<ReviewData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_design, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        String title = data.get(position).getUser();
        String desc = data.get(position).getText();

        holder.textViewTitle.setText(title);
        holder.textViewDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewDesc;
        private CardView cardView;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.reviewTitle);
            textViewDesc = itemView.findViewById(R.id.reviewText);
            cardView = itemView.findViewById(R.id.reviewCardWidget);
        }
    }
}
