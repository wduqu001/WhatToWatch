package com.wduqu001.android.whattowatch.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.wduqu001.android.whattowatch.R;
import com.wduqu001.android.whattowatch.data.MoviesContract;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

import java.net.URL;

import static com.wduqu001.android.whattowatch.utilities.DataUtils.getMoviesFromCursor;
import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.getMoviesContent;
import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.getResponseFromHttpUrl;

/**
 * Task responsive for Querying the api for movie data
 */
public class MovieQueryTask extends AsyncTask<String, Void, ContentValues[]> {
    private final QueryTaskCompleteListener<ContentValues[]> mTaskCompleteListener;
    private final Context mContext;

    public MovieQueryTask(QueryTaskCompleteListener<ContentValues[]> listener, Context context) {
        this.mTaskCompleteListener = listener;
        this.mContext = context;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     */
    @Override
    protected ContentValues[] doInBackground(String... params) {
        String option = params[0];
        if (option.equals(mContext.getString(R.string.option_favorites))) {
            Cursor cursor = mContext.getContentResolver().query(
                    MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return getMoviesFromCursor(cursor);
            }
        }
        if (NetworkUtils.isOnline()) {
            URL url = NetworkUtils.buildMoviesUrl(params);
            String moviesApiResult = getResponseFromHttpUrl(url);
            return getMoviesContent(moviesApiResult, option);
        }
        return null;
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}.
     * The specified result is the value returned by {@link #doInBackground}.
     */
    @Override
    protected void onPostExecute(ContentValues[] contentValues) {
        super.onPostExecute(contentValues);
        mTaskCompleteListener.onTaskComplete(contentValues);
    }
}
