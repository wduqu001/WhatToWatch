package com.wduqu001.android.whattowatch.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.wduqu001.android.whattowatch.data.MoviesContract;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

import java.net.URL;

public class MovieSyncTask {

    public synchronized static void sync(Context context) {
        if (!NetworkUtils.isOnline()) {
            return;
        }
        // TODO: Change it to read a list of saved movie IDs from the preferences and fetch the movie info
        int option = 1;
        URL url;
        String jsonResponse;
        ContentValues[] moviesValues;
        //TODO: Add method to read favorite movie list
        url = NetworkUtils.buildMoviesUrl(option);
        jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
        moviesValues = NetworkUtils.getMoviesList(jsonResponse);

        // if there is data in moviesValues update contentResolver's content
        if (moviesValues != null && moviesValues.length != 0) {
            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.delete(MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null);

            contentResolver.bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,
                    moviesValues);
        }
    }
}
