package com.example.bookreviewsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;


public class AddBookActivity extends AppCompatActivity {
    private EditText titleText, authorText, yearOfPublishText;
    private Spinner isReadSpinner;
    private Button submitButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_books_activity);

        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        yearOfPublishText = findViewById(R.id.yearOfPublishText);
        isReadSpinner = findViewById(R.id.isReadSpinner);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        //values of the book to add
        String title = titleText.getText().toString();
        String author = authorText.getText().toString();
        String year = yearOfPublishText.getText().toString();
        String isRead = isReadSpinner.getSelectedItem().toString();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    DatabaseHelper dbHelper = null;
                    try {
                        dbHelper = new DatabaseHelper(getApplicationContext());
                        boolean isInputValid = dbHelper.validateInput(title, author,
                                year, isRead);
                        if (!isInputValid){
                            throw new SQLException("The input is not valid! Please try again!");
                        }
                        dbHelper.insert(title, author,
                                year, isRead);
                        Toast.makeText(AddBookActivity.this, "You just added " + titleText.getText().toString() + " by " + authorText +
                                "successfully!" + LocalDate.now().getYear(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddBookActivity.this, MainActivity.class);
                        startActivity(intent);

                    } catch (SQLException ex) {
                        Toast.makeText(getApplicationContext(), ex.getLocalizedMessage(), Toast.LENGTH_LONG)
                                .show();
                    } finally {
                        if (dbHelper != null) {
                            dbHelper.close();
                        }
                    }
                } else {
                    Toast.makeText(AddBookActivity.this, "All fields must be filled! The year " +
                            "of publish should be > 0 and < " + LocalDate.now().getYear(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddBookActivity.this)
                        .setTitle("Cancel")
                        .setMessage("Are you sure you want to cancel adding this book?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Intent intent = new Intent(AddBookActivity.this, MainActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    private boolean validateFields() {
        if (titleText.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (authorText.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (isReadSpinner.getSelectedItemPosition() == 0) {
            return false;
        }
        if (!yearOfPublishText.getText().toString().trim().isEmpty()) {
            int year = Integer.parseInt(yearOfPublishText.getText().toString().trim());
            if (year <= 0 || year > LocalDate.now().getYear()) {
                return false;
            }
        }
        return true;
    }
}
