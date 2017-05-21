package com.wduqu001.android.whattowatch.utilities;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.wduqu001.android.whattowatch.BuildConfig;
import com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    public final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private final static String LANGUAGE = Locale.getDefault().getLanguage().concat("-").concat(Locale.getDefault().getCountry());
    private final static int PAGES = 1;
    public final static String POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    final static String REVIEWS = "reviews";
    final static String VIDEOS = "videos";

    /**
     * * Builds a url to query movie data
     *
     * @param option Selected content to be requested from the api"
     * @return a new url for the tmdb api
     */
    public static URL buildMoviesUrl(String option) {

        if (option.isEmpty()) option = POPULAR;
        try {
            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendPath(option)
                    .appendQueryParameter("api_key", TMDB_API_KEY)
                    .appendQueryParameter("language", LANGUAGE)
                    .appendQueryParameter("page", Integer.toString(PAGES))
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Builds a object of type ContentValues[] with movies content from a String ( resulted from a http request)
     *
     * @param StringParam http result in a String
     * @return A list of movies
     */
    public static ContentValues[] getMoviesList(String StringParam) {

        JSONArray moviesArray;
        try {
            moviesArray = new JSONObject(StringParam).getJSONArray("results");

            if (moviesArray == null) {
                return null;
            }

            ContentValues[] contentValues = new ContentValues[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieData = moviesArray.getJSONObject(i);
                contentValues[i] = getMovieFromJson(movieData);
            }
            return contentValues;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a ContentValues with Movie content from JSON
     *
     * @param movieData JSONObject from with Movie will be extracted
     * @return ContentValues
     * @throws JSONException
     */
    private static ContentValues getMovieFromJson(JSONObject movieData) throws JSONException {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesEntry.COLUMN_MOVIE_ID, movieData.getString("id"));
        movieValues.put(MoviesEntry.COLUMN_TITLE, movieData.getString("title"));
        movieValues.put(MoviesEntry.COLUMN_BACKDROP_PATH, movieData.getString("backdrop_path"));
        movieValues.put(MoviesEntry.COLUMN_ORIGINAL_TITLE, movieData.getString("original_title"));
        movieValues.put(MoviesEntry.COLUMN_OVERVIEW, movieData.getString("overview"));
        movieValues.put(MoviesEntry.COLUMN_POSTER_PATH, movieData.getString("poster_path"));
        movieValues.put(MoviesEntry.COLUMN_RELEASE_DATE, movieData.getString("release_date"));
        movieValues.put(MoviesEntry.COLUMN_VOTE_AVERAGE, movieData.getString("vote_average"));

        return movieValues;
    }

    /**
     * Checks for internet connection.
     * Method based on solution available at http://stackoverflow.com/a/27312494/5988277
     *
     * @return false if internet connection is not available
     */
    public static boolean isOnline() {
        int timeoutMs = 3500;
        Socket socket = new Socket();
        try {
            SocketAddress socketAddress = new InetSocketAddress("themoviedb.org", 80);
            socket.connect(socketAddress, timeoutMs);
            socket.close();

            return true;
        } catch (IOException e) {
            Log.e("isOnline", "failed to connect to themoviedb.org (port 80) after 3000ms");
            return false;
        }
    }
}