package com.example.bookreviewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookreviewsapp.entity.view.BookView;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        BookView bookView = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        TextView isRead = convertView.findViewById(R.id.isRead);
        Button editBtn = convertView.findViewById(R.id.buttonEdit);
        Button deleteBtn = convertView.findViewById(R.id.buttonDelete);

        if (position == 0) {
            title.setText("Title");
            author.setText("Author");
            isRead.setText("Have I read it");
            editBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        } else {
            title.setText(bookView.getTitle());
            author.setText(bookView.getAuthor());
            isRead.setText(bookView.getIsRead());
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}
