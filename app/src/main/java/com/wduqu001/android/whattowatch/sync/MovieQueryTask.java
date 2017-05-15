package com.wduqu001.android.whattowatch.sync;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.wduqu001.android.whattowatch.MainActivity;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

import java.net.URL;

import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.getMoviesList;
import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.getResponseFromHttpUrl;

/**
 * Task responsive for Querying the api for movie data
 */
public class MovieQueryTask extends AsyncTask<URL, Void, ContentValues[]> {
    private final QueryTaskCompleteListener<ContentValues[]> mTaskCompleteListener;

    public MovieQueryTask(MainActivity.TaskCompleteListener listener) {
        this.mTaskCompleteListener = listener;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     */
    @Override
    protected ContentValues[] doInBackground(URL... params) {
        URL url = params[0];
        String moviesApiResult;
        if (NetworkUtils.isOnline()) {
            moviesApiResult = getResponseFromHttpUrl(url);
            return getMoviesList(moviesApiResult);
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
