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
        searchBookInApi = findViewById(R.id.searchBookInOpenLibraryButton);
        booksList = findViewById(R.id.booksList);
        noBooksText = findViewById(R.id.noBooksText);

        //redirects to the Add book activity
        addNewBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
            startActivity(intent);
        });
        //redirects to the Search book activity
        searchBookInApi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
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

            List<BookView> bookViews = dbHelper.selectAllBooks();
            if (bookViews != null && !bookViews.isEmpty()){
                setBooks(bookViews);
            }

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
        CustomArrayAdapter customArrayAdapter =
                new CustomArrayAdapter(this,
                        R.layout.book_item,
                        this.books);
        booksList.setAdapter(customArrayAdapter);
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