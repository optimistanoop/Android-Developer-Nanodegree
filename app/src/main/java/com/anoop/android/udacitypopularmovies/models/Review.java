package com.anoop.android.udacitypopularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.anoop.android.udacitypopularmovies.db.MovieContract;
import com.google.gson.annotations.Expose;
import org.parceler.Parcel;

@Parcel
public class Review {
    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;
    @Expose
    private String url;

    public Review(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, getAuthor());
        values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, getContent());
        values.put(MovieContract.ReviewEntry.COLUMN_API_ID, getId());
        values.put(MovieContract.ReviewEntry.COLUMN_URL, getUrl());
        return values;
    }


    public static final String[] ALL_COLUMN_PROJECTION = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_URL,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_API_ID
    };

    public static final int COL_ID = 0;
    public static final int COL_CONTENT = 1;
    public static final int COL_URL = 2;
    public static final int COL_AUTHOR = 3;
    public static final int COL_API_ID = 4;

    public Review(Cursor cursor)
    {
        setId(cursor.getString(COL_API_ID));
        setContent(cursor.getString(COL_CONTENT));
        setUrl(cursor.getString(COL_URL));
        setAuthor(cursor.getString(COL_AUTHOR));
    }
}
