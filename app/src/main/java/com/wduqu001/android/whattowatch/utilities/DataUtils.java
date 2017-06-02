package com.wduqu001.android.whattowatch.utilities;

import android.content.ContentValues;
import android.database.Cursor;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_AVERAGE_RANKING;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;

public class DataUtils {

    public static ContentValues getMovieContentFromCursor(Cursor cursor) {
        ContentValues movieContent = new ContentValues();
        movieContent.put(COLUMN_MOVIE_ID, cursor.getString(cursor.getColumnIndex(COLUMN_MOVIE_ID)));
        movieContent.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        movieContent.put(COLUMN_BACKDROP_PATH, cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
        movieContent.put(COLUMN_ORIGINAL_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)));
        movieContent.put(COLUMN_OVERVIEW, cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
        movieContent.put(COLUMN_POSTER_PATH, cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
        movieContent.put(COLUMN_RELEASE_DATE, cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
        movieContent.put(COLUMN_AVERAGE_RANKING, cursor.getString(cursor.getColumnIndex(COLUMN_AVERAGE_RANKING)));

        return movieContent;
    }

    public static ContentValues[] getMoviesFromCursor(Cursor cursor) {
        ContentValues[] movies = new ContentValues[cursor.getCount()];
        if(cursor.moveToFirst()){
            int i = 0;
             while (!cursor.isAfterLast()) {
                 movies[i++] = getMovieContentFromCursor(cursor);
                 cursor.moveToNext();
             }
         }
        return movies;
    }
}
