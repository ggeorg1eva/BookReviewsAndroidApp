package com.example.bookreviewsapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "books.db";
    public static final int DB_VERSION = 1;

    private SQLiteDatabase database;
    public static final String CREATE_BOOKS_TABLE_QUERY = "CREATE TABLE books (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "author TEXT NOT NULL, " +
            "yearOfPublish INTEGER DEFAULT 0, " +
            "isRead TEXT NOT NULL, " +
            "review TEXT DEFAULT 'No review yet', " +
            "UNIQUE (title, author), " +
            "CHECK (isRead IN ('Yes', 'No'))" +
            ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOKS_TABLE_QUERY);

    }

    public void insert(String title, String author,
                       String yearOfPublish, String isRead){
        String insertQuery = "INSERT INTO books(" +
                "title, author, yearOfPublish, isRead" +
                ") VALUES(?, ?, ?, ?)";
        database.execSQL(insertQuery, new Object[]{
                title, author, yearOfPublish, isRead
        });
    }

    public void update(Book book){
        String updateQuery = "UPDATE books SET " +
                "title = ?, author = ?, yearOfPublish = ?, isRead = ?, review = ? " +
                " WHERE  id = ? ";
        database.execSQL(updateQuery, new Object[]{
                book.getTitle(), book.getAuthor(), book.getYearOfPublish(),
                book.getIsRead(), book.getReview(), book.getId()
        });
    }

    public void delete(Book book){
        String deleteQuery = "DELETE FROM books WHERE id = ?; ";
        database.execSQL(deleteQuery, new Object[]{book.getId()});

    }

    public List<BookView> selectAllBooks(){
        String selectQuery = "SELECT * FROM books ORDER BY isRead; ";

        Cursor c = database.rawQuery(selectQuery, null);

        List<Book> books = new ArrayList<>();

        while (c.moveToNext()){
            @SuppressLint("Range") Book book = new Book(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("author")),
                    c.getInt(c.getColumnIndex("yearOfPublish")),
                    c.getString(c.getColumnIndex("isRead")),
                    c.getString(c.getColumnIndex("review"))
            );

            books.add(book);
        }
        c.close();

        ModelMapper mapper = new ModelMapper();
        List<BookView> bookViewsList = books.stream()
                .map(b -> mapper.map(b, BookView.class)).collect(Collectors.toList());

        return bookViewsList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion>oldVersion){
            sqLiteDatabase.execSQL("DROP TABLE books; ");
            sqLiteDatabase.execSQL(CREATE_BOOKS_TABLE_QUERY);
        }
    }

    private void validateInput(String[] input){
        //todo implement
    }
}

