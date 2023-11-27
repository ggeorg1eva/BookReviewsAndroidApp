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
    private List<BookView> books;

    private DatabaseHelper dbHelper;

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

        getBooksFromDB();

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
        getBooksFromDB();
    }

    private void getBooksFromDB() {
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

    private void populateListView() throws Exception{
        dbHelper = new DatabaseHelper(getApplicationContext());
        List<BookView> books = dbHelper.selectAllBooks();
        //todo use logic from chat gpt with custom adapter
        CustomArrayAdapter customArrayAdapter =
                new CustomArrayAdapter(this,
                        R.layout.book_item,
                        books);
        booksList.setAdapter(customArrayAdapter);
//        listView.clearChoices();
//        listView.setAdapter(customArrayAdapter);
//        db.close();
//        db = null;

    }


    private void setBooks(List<BookView> books) {
        this.books = books;
    }
}