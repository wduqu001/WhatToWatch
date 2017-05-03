package com.wduqu001.android.whattowatch;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.wduqu001.android.whattowatch.NetworkUtils.getMoviesList;
import static com.wduqu001.android.whattowatch.NetworkUtils.getResponseFromHttpUrl;

/**
 * Task responsive for Querying the api for movie data
 */
class MovieQueryTask extends AsyncTask<URL, Void, List<Movie>> {
    private final QueryTaskCompleteListener<List<Movie>> mTaskCompleteListener;

    MovieQueryTask(MainActivity.TaskCompleteListener listener) {
        this.mTaskCompleteListener = listener;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     */
    @Override
    protected List<Movie> doInBackground(URL... params) {
        URL url = params[0];
        String moviesApiResult;
        List<Movie> mMovies = null;
        if (NetworkUtils.isOnline()) {
            try {
                moviesApiResult = getResponseFromHttpUrl(url);
                mMovies = getMoviesList(moviesApiResult);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return mMovies;
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}.
     * The specified result is the value returned by {@link #doInBackground}.
     */
    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        mTaskCompleteListener.onTaskComplete(movies);
    }
}
