package com.example.bookreviewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookreviewsapp.entity.view.BookView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addNewBookButton;
    private Button searchBookInApi;
    private ListView booksList;
    private TextView noBooksText;
    private List<BookView> books;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewBookButton = findViewById(R.id.addNewBookButton);
        searchBookInApi = findViewById(R.id.searchBookInGoogleBooks);
        booksList = findViewById(R.id.booksList);
        noBooksText = findViewById(R.id.noBooksText);
        addNewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        searchBookInApi.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditDeleteBookActivity.class);
                startActivity(intent);
            }
        });

        getBooksFromDB();

        if (books != null && !books.isEmpty()) {
            this.populateListView();
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

    /**
     * Gets all books from BD in the form of BookView and populates the books variable with them.
     */
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

    private void populateListView(){
        //todo add dummy book to books so that first line of list are titles of columns

        CustomArrayAdapter customArrayAdapter =
                new CustomArrayAdapter(this,
                        R.layout.book_item,
                        this.books);
        booksList.setAdapter(customArrayAdapter);
//        listView.clearChoices();
//        listView.setAdapter(customArrayAdapter);
//        db.close();
//        db = null;

    }

    /**
     * Sets the books field to have 1st element empty, so that in the custom Adapter class
     * the first element of the BooksList can be the name of the columns;
     * then populates the book field with the books from the DB as BookView instances
     * @param books
     */
    private void setBooks(List<BookView> books) {
        this.books = new ArrayList<>();
        this.books.add(new BookView());
        this.books.addAll(books);
    }
}