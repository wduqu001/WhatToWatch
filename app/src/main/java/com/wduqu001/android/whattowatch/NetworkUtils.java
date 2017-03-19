package com.wduqu001.android.whattowatch;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private final static String TMDB_POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TMDB_TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private final static String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private final static String LANGUAGE = Locale.getDefault().getLanguage().concat("-").concat(Locale.getDefault().getCountry());
    private final static int PAGES = 1;

    /**
     * * Builds a url for PopularMovies
     *
     * @param option Choose between "most popular movies(0)" and "top rated movies (1)"
     * @return a new url for the tmdb api
     * @throws MalformedURLException
     */
    static URL buildMoviesUrl(int option) throws MalformedURLException {
        String base = TMDB_POPULAR_MOVIES_URL;

        if (option == 1) base = TMDB_TOP_RATED_URL;

        Uri builtUri = Uri.parse(base).buildUpon()
                .appendQueryParameter("api_key", TMDB_API_KEY)
                .appendQueryParameter("language", LANGUAGE)
                .appendQueryParameter("page", Integer.toString(PAGES))
                .build();

        return new URL(builtUri.toString());
    }

    static String getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

            Response response = client.newCall(request).execute();
            return response.body().string();
    }

    /**
     * Builds a object of type List<Movie> from a String ( resulted from a http request)
     *
     * @param StringParam http result in a String
     * @return A list of Movies
     * @throws JSONException
     */
    static List<Movie> getMoviesList(String StringParam) throws JSONException {

        JSONArray moviesArray;
        List<Movie> movieList = null;

        moviesArray = new JSONObject(StringParam).getJSONArray("results");

        if (moviesArray == null) {
            return null;
        }
        movieList = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieData = moviesArray.getJSONObject(i);
            Movie movie = getMovieFromJson(movieData);
            movieList.add(i, movie);
        }

        return movieList;
    }

    /**
     * Creates a Movie object from JSON
     *
     * @param movieData JSONObject from with Movie will be extracted
     * @return Movie
     * @throws JSONException
     */
    @NonNull
    private static Movie getMovieFromJson(JSONObject movieData) throws JSONException {
        Movie movie = new Movie(
                String.valueOf(movieData.getInt("id")),
                movieData.getString("title"),
                movieData.getString("poster_path"),
                movieData.getString("backdrop_path")
        );
        movie.setOriginalTitle(movieData.getString("original_title"));
        movie.setOverview(movieData.getString("overview"));
        movie.setVote_average(movieData.getDouble("vote_average"));
        movie.setRelease_date(movieData.getString("release_date"));
        return movie;
    }

    /**
     * Checks for internet connection.
     * Method based on solution available at http://stackoverflow.com/a/27312494/5988277
     *
     * @return false if internet connection is not available
     */
    static boolean isOnline() {
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
