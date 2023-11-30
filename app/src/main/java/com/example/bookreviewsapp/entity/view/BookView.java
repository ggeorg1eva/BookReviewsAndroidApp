package com.example.bookreviewsapp.entity.view;

public class BookView {
    private Integer id;
    private String title;
    private String author;
    private Integer yearOfPublish;
    private String isRead;
    public BookView(){}

    public BookView(Integer id, String title, String author, Integer yearOfPublish, String isRead) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.yearOfPublish = yearOfPublish;
        this.isRead = isRead;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYearOfPublish() {
        return yearOfPublish;
    }

    public void setYearOfPublish(Integer yearOfPublish) {
        this.yearOfPublish = yearOfPublish;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
