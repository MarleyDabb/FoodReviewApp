package com.example.foodreview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.foodreview.models.ReviewData;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ReviewData> {

    Context context;
    int layoutResourceId;
    List<ReviewData> data = null;

    public CustomAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<ReviewData> objects) {
        super(context, resource, textViewResourceId, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReviewData review = null;

//        if(row == null)
//        {
//            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//            row = inflater.inflate(layoutResourceId, parent, false);
//
//
//            review = new ReviewData();
//            review.us = (TextView) row.findViewById();
//            holder.to = (TextView)row.findViewById(R.id.toTextView);
//            holder.subject = (TextView)row.findViewById(R.id.subjectTextView);
//
//            row.setTag(holder);
//        }
//        else
//        {
//            holder = (HeaderHolder) row.getTag();
//        }
//
//        Headers item = data.get(position);
//        holder.from.setText(item.getFrom());
//        holder.to.setText(item.getTo());
//        holder.subject.setText(item.getSubject());

        return row;
    }
}

