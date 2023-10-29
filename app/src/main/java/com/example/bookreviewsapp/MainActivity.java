package com.example.bookreviewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addNewBookButton;
    private Button editDeleteBooksButton;
    private ListView booksList;
    private TextView noBooksText;

    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewBookButton = findViewById(R.id.addNewBookButton);
        editDeleteBooksButton = findViewById(R.id.editDeleteButton);
        booksList = findViewById(R.id.booksList);
        noBooksText = findViewById(R.id.noBooksText);
        addNewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        editDeleteBooksButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditDeleteBookActivity.class);
                startActivity(intent);
            }
        });

        fillBooksList();

        if (books != null && !books.isEmpty()) {
            booksList.setVisibility(View.VISIBLE);
            noBooksText.setVisibility(View.GONE);
        } else {
            booksList.setVisibility(View.GONE);
            noBooksText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //every time a book is added/edited/deleted it will refresh the table
        fillBooksList();
    }

    private void fillBooksList() {
        DatabaseHelper dbHelper = null;
        try {
            dbHelper = new DatabaseHelper(getApplicationContext());

            setBooks(dbHelper.selectAllBooks());

        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    private void setBooks(List<Book> books) {
        this.books = books;
    }
}