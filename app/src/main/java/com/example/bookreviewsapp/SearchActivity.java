package com.example.bookreviewsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;

public class SearchActivity extends AppCompatActivity {
    private static final String DEFAULT_SPINNER_MESSAGE = "Choose how many books you want to see";
    private static final String OPEN_LIBRARY_SEARCH_BY_SUBJECT_API = "https://openlibrary.org/search.json?subject=";
    private static final String OPEN_LIBRARY_LIMIT_URL = "&limit=";
    private static final String NEXT_LINE_SYMBOL = "\n";
    private Button submitBtn, goBackBtn;
    private EditText subjectText;
    private Spinner booksCountSpinner;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_in_open_lib);

        submitBtn = findViewById(R.id.submitButton);
        goBackBtn = findViewById(R.id.goBackButton);
        subjectText = findViewById(R.id.subjectText);
        booksCountSpinner = findViewById(R.id.booksCountSpinner);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setText("No books searched yet");

        
        submitBtn.setOnClickListener(v -> {
            String subject = subjectText.getText().toString();
            String booksCountStr = booksCountSpinner.getSelectedItem().toString();
            if (isInputValid(subject, booksCountStr)){
                int booksCount = Integer.parseInt(booksCountStr);
                searchInOpenLib(subject, booksCount);
            } else {
                Toast.makeText(SearchActivity.this, "You must enter a subject and a number of books!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        goBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    /**
     * This method opens a new Thread so that it doesn't block the main UI thread.
     * It performs the search on Open Library by using a prepared URL string.
     * URLEncoder.encode(subject, "UTF-8") makes it safe for URL.
     * @param subject - subject chosen by the user
     * @param booksCount - number of books the user wants displayed
     */
    private void searchInOpenLib(String subject, int booksCount) {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            try {
                String urlString = OPEN_LIBRARY_SEARCH_BY_SUBJECT_API + URLEncoder.encode(subject, "UTF-8")
                        + OPEN_LIBRARY_LIMIT_URL + booksCount;
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                //try-with-resources for InputStream
                try (InputStream is = new BufferedInputStream(urlConnection.getInputStream())) {
                    String response = readResponse(is);
                    parseJsonAndDisplayResult(response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                updateUI("Error occurred while fetching data");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }

    /**
     * This method iterates through the docs array of the json response.
     * This is a standard element of the json, which the OpenLibrary API returns.
     * In it are all book objects.
     * The title, author_name and first_publish_year are some of the fields of the book objects.
     * @param json - the response from the Open Library search
     */
    private void parseJsonAndDisplayResult(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray docs = jsonObject.getJSONArray("docs");
            StringBuilder resultBuilder = new StringBuilder();

            for (int i = 0; i < docs.length(); i++) {
                JSONObject book = docs.getJSONObject(i);
                String title = book.getString("title");
                JSONArray authors = book.optJSONArray("author_name");
                String firstPublishYear = book.getString("first_publish_year");

                resultBuilder.append("Title: ")
                        .append(title)
                        .append(NEXT_LINE_SYMBOL);

                if (authors != null) {
                    resultBuilder.append("Author/s: ");

                    for (int j = 0; j < authors.length(); j++) {
                        resultBuilder.append(authors.getString(j));
                        if (j < authors.length() - 1) {
                            resultBuilder.append(", ");
                        }
                    }
                }
                resultBuilder.append(NEXT_LINE_SYMBOL)
                        .append("Year of first publish: ")
                        .append(firstPublishYear)
                        .append(NEXT_LINE_SYMBOL)
                        .append(NEXT_LINE_SYMBOL);
            }

            updateUI(resultBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            updateUI("Failed to parse the JSON response");
        }
    }

    /**
     * Sets the text of the result TextView with either resulting books or error message
     * @param resultString
     */
    private void updateUI(String resultString) {
        runOnUiThread(() -> {
            resultTextView.setText(resultString);
        });
    }

    /**
     * Reads the response from the API to OpenLibrary
     * @param is InputStream
     * @return the response (json), converted to String
     * @throws IOException
     */
    private String readResponse(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Validates that the input subject is ok and that a book count has been selected.
     * The subject is further validated by the URLEncoder.encode(subject, "UTF-8"), which makes the string safe for
     * usage in a URL
     * @param subject
     * @param booksCountStr
     * @return true if input is valid and false if it is not
     */
    private boolean isInputValid(String subject, String booksCountStr) {
        if (subject == null || subject.trim().isEmpty()){
            return false;
        }
        if (booksCountStr == null || booksCountStr.trim().isEmpty() || booksCountStr.equalsIgnoreCase(DEFAULT_SPINNER_MESSAGE)){
            return false;
        }
        return true;
    }
}
