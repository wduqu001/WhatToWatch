package com.wduqu001.android.whattowatch.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wduqu001.android.whattowatch.data.MoviesContract;

import static com.wduqu001.android.whattowatch.utilities.DataUtils.getMovieContentFromCursor;

public class AsyncDbTasks extends AsyncTask<String, Void, ContentValues> {
    public static final String QUERY = "query";
    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    private final QueryTaskCompleteListener<ContentValues> mTaskCompleteListener;
    private final Context mContext;
    private final ContentValues mValues;

    public AsyncDbTasks(QueryTaskCompleteListener<ContentValues> listener, Context context, @Nullable ContentValues values) {
        this.mTaskCompleteListener = listener;
        this.mContext = context;
        this.mValues = values;
    }

    @Override
    protected ContentValues doInBackground(@NonNull String... params) {
        String option = params[0];
        String movieId = null;
        ContentValues result = new ContentValues();
        int rowsAffected;
        // TODO: validate necessity for the variable bellow (uri)
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        if (params.length > 1 && params[1] != null) {
            movieId = params[1];
            uri = Uri.parse(MoviesContract.MoviesEntry.CONTENT_URI + movieId);
        }
        switch (option) {
            case QUERY:
                // Only with id
                if (movieId == null) {
                    return null;
                }
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    return getMovieContentFromCursor(cursor);
                }
                break;
            case DELETE:
                rowsAffected = mContext.getContentResolver().delete(uri, null, null);
                if (rowsAffected > 0) {
                    result.put("result", rowsAffected);
                    return result;
                }
                break;
            case INSERT:
                Uri resultUri = mContext.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, mValues);
                if (resultUri != null) {
                    result.put("result", resultUri.toString());
                    return result;
                }
                break;
            case UPDATE:
                rowsAffected = mContext.getContentResolver().update(uri, mValues, null, null);
                if (rowsAffected > 0) {
                    result.put("result", rowsAffected);
                    return result;
                }
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ContentValues result) {
        super.onPostExecute(result);
        mTaskCompleteListener.onTaskComplete(result);
    }
}
