package com.wduqu001.android.whattowatch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.TABLE_NAME;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "waitlist.db";
    private static final int DATABASE_VERSION = 1;

    private final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_POSTER_PATH + " TEXT, " +
            COLUMN_BACKDROP_PATH + " TEXT, " +
            COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL " +
            "); ";

//    private static final String DATABASE_ALTER_NEW_COLUMN = "ALTER TABLE "
//            + TABLE_NAME + " ADD COLUMN " + NEW_COLUMN + "  TEXT;";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        // After changing the db schema change the code above to
//        if (oldVersion < 2) {
//            db.execSQL(DATABASE_ALTER_NEW_COLUMN);
//        }
    }
}
