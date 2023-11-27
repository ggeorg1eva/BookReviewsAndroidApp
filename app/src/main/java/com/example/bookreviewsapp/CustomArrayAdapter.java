package com.example.bookreviewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<BookView> {
    private Context context;
    private int resource;


    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<BookView> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BookView bookView = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource,
                    parent, false);
        }
        TextView title =convertView.findViewById(R.id.title);
        title.setText(bookView.getTitle());
        TextView author =convertView.findViewById(R.id.author);
        author.setText(bookView.getAuthor());
        TextView isRead =convertView.findViewById(R.id.isRead);
        isRead.setText(bookView.getIsRead());
        //TextView review =convertView.findViewById(R.id.review);
        //review.setText(bookView.getReview());
        return  convertView;

    }

}
