package com.wduqu001.android.whattowatch.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.CONTENT_URI;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.TABLE_NAME;

@SuppressWarnings("ConstantConditions")
public class MoviesContentProvider extends ContentProvider {
    // Define final integer constants for the directory of movies and a single item.
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mDbHelper;
    private ContentResolver mContentResolver;

    /**
     * Initialize a new matcher object without any matches,
     * then use .addURI(String authority, String path, int match) to add matches
     */
    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /*
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        mContentResolver = getContext().getContentResolver();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {

        // Get access to the database
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        final boolean distinct = true;
        final String groupBy = null;
        final String having = null;
        final String limit = null;

        switch (match) {
            case MOVIES:
                retCursor = db.query(TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        orderBy);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                if (selection.isEmpty()) {
                    selection = COLUMN_MOVIE_ID + "=" + id;
                }
                retCursor = db.query(distinct,
                        TABLE_NAME,
                        columns,
                        selection,
                        new String[]{id},
                        groupBy,
                        having,
                        orderBy,
                        limit);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(mContentResolver, uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case MOVIES:
                try {
                    long id = db.insertOrThrow(TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                    }
                }catch (SQLiteConstraintException e){
                    Log.d("MoviesContentProvider","Failed on insert operation. Is the content already on DB ? " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();
        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }
        db.close();
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int moviesUpdated;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesUpdated = mDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }
        return moviesUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}