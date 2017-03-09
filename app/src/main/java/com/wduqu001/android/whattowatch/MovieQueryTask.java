package com.wduqu001.android.whattowatch;

/**
 * Created by willian on 3/8/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;
import java.util.List;

/**
 * Task responsive for Querying the api for movie data
 */
public class MovieQueryTask extends AsyncTask<URL, Void, List<Movie>> {
    private final Context mContext;
    private final QueryTaskCompleteListener<List<Movie>> mTaskCompleteListener;


    public MovieQueryTask(Context context, MainActivity.TaskCompleteListener listener) {
        this.mContext = context;
        this.mTaskCompleteListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
            moviesApiResult = NetworkUtils.getResponseFromHttpUrl(url);
            mMovies = NetworkUtils.getMoviesList(moviesApiResult);
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
