package com.wduqu001.android.whattowatch.utilities;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.wduqu001.android.whattowatch.BuildConfig;
import com.wduqu001.android.whattowatch.data.MoviesContract;
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

    public final static String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private final static String LANGUAGE = Locale.getDefault().getLanguage().concat("-").concat(Locale.getDefault().getCountry());
    private final static int PAGES = 1;
    public final static String POPULAR = "popular"; // https://api.themoviedb.org/3/movie/popular?
    public final static String TOP_RATED = "top_rated"; // https://api.themoviedb.org/3/movie/top_rated?
    public final static String REVIEWS = "reviews"; // https://api.themoviedb.org/3/movie/283995/reviews?
    public final static String VIDEOS = "videos"; // https://api.themoviedb.org/3/movie/283995/videos?

    /**
     * * Builds a url to query movie data
     *
     * @param params Selected content to be requested from the api"
     * @return a new url for the tmdb api
     */
    public static URL buildMoviesUrl(String... params) {
        String option = params[0];

        if (option.isEmpty()) option = POPULAR;
        try {
            if(params.length > 1){
                String id = params[1];
                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(id)
                        .appendPath(option)
                        .appendQueryParameter("api_key", TMDB_API_KEY)
                        .appendQueryParameter("language", LANGUAGE)
                        .appendQueryParameter("page", Integer.toString(PAGES))
                        .build();

                return new URL(builtUri.toString());
            }
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
     * Builds a object of type ContentValues[] with extra content for a movie from a http request result
     *
     * @return Movie content values
     */
    public static ContentValues[] getMoviesContent(String stringParam, String desiredContent) {
        JSONArray moviesArray;
        try {
            moviesArray = new JSONObject(stringParam).getJSONArray("results");

            if (moviesArray == null) {
                return null;
            }

            ContentValues[] contentValues = new ContentValues[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieData = moviesArray.getJSONObject(i);
                switch (desiredContent) {
                    case REVIEWS:
                        contentValues[i] = getReviewsFromJson(movieData);
                        break;
                    case VIDEOS:
                        contentValues[i] = getVideosFromJson(movieData);
                        break;
                    default:
                        contentValues[i] = getMovieFromJson(movieData);
                        break;
                }
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
     * @return ContentValues The movie data
     * @throws JSONException in case of a non-readable json object
     */
    private static ContentValues getMovieFromJson(JSONObject movieData) throws JSONException {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieData.getString("id"));
        movieValues.put(MoviesEntry.COLUMN_TITLE, movieData.getString("title"));
        movieValues.put(MoviesEntry.COLUMN_BACKDROP_PATH, movieData.getString("backdrop_path"));
        movieValues.put(MoviesEntry.COLUMN_ORIGINAL_TITLE, movieData.getString("original_title"));
        movieValues.put(MoviesEntry.COLUMN_OVERVIEW, movieData.getString("overview"));
        movieValues.put(MoviesEntry.COLUMN_POSTER_PATH, movieData.getString("poster_path"));
        movieValues.put(MoviesEntry.COLUMN_RELEASE_DATE, movieData.getString("release_date"));
        movieValues.put(MoviesEntry.COLUMN_AVERAGE_RANKING, movieData.getString("vote_average"));

        return movieValues;
    }

    private static ContentValues getReviewsFromJson(JSONObject data) throws JSONException {
        ContentValues reviewContent = new ContentValues();
        reviewContent.put("id", data.getString("id"));
        reviewContent.put("author", data.getString("author"));
        reviewContent.put("content", data.getString("content"));
        reviewContent.put("url", data.getString("url"));

        return reviewContent;
    }

    private static ContentValues getVideosFromJson(JSONObject data) throws JSONException {
        ContentValues videosContent = new ContentValues();
        videosContent.put("id", data.getString("id"));
        videosContent.put("key", data.getString("key"));
        videosContent.put("name", data.getString("name"));
        videosContent.put("site", data.getString("site"));
        videosContent.put("language",
                String.format("%s-%s", data.getString("iso_639_1"), data.getString("iso_3166_1"))
        );

        return videosContent;
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