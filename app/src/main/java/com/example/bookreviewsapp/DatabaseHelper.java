package com.example.bookreviewsapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bookreviewsapp.entity.Book;
import com.example.bookreviewsapp.entity.view.BookView;

import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "books.db";
    public static final int DB_VERSION = 2;

    private SQLiteDatabase database;
    public static final String CREATE_BOOKS_TABLE_QUERY = "CREATE TABLE books (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "author TEXT NOT NULL, " +
            "yearOfPublish INTEGER DEFAULT 0, " +
            "isRead TEXT NOT NULL, " +
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

    /**
     * Checks whether the input book has valid parameters
     * @return false if the input is invalid
     * @return true if it is ok
     */
    public boolean validateInput(String title, String author, String yearOfPublish, String isRead){
        if(title == null || title.trim().isEmpty()){
            return false;
        }
        if(author == null || author.trim().isEmpty()){
            return false;
        }
        if(yearOfPublish == null || yearOfPublish.trim().isEmpty()
                || Integer.parseInt(yearOfPublish) < 0
                || Integer.parseInt(yearOfPublish) > LocalDate.now().getYear()){
            return false;
        }
        if(isRead == null || isRead.trim().isEmpty() || (!isRead.equals("Yes") && !isRead.equals("No"))){
            return false;
        }

        return true;
    }

    public void update(BookView book){
        String updateQuery = "UPDATE books SET " +
                "title = ?, author = ?, yearOfPublish = ?, isRead = ? " +
                " WHERE  id = ? ";
        database.execSQL(updateQuery, new Object[]{
                book.getTitle(), book.getAuthor(), book.getYearOfPublish(),
                book.getIsRead(), book.getId()
        });
    }

    public void delete(int bookId){
        String deleteQuery = "DELETE FROM books WHERE id = ?; ";
        database.execSQL(deleteQuery, new Object[]{bookId});

    }

    /**
     * Gets all books currently saved in the DB and sorts them by id ascending.
     * @return returns a list of the books, but of type BookView which serves as a DTO between the layers of the application.
     */
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
                    c.getString(c.getColumnIndex("isRead"))
            );

            books.add(book);
        }
        c.close();

        ModelMapper mapper = new ModelMapper();
        List<BookView> bookViewsList = books.stream()
                .sorted(Comparator.comparing(Book::getId))
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

}

