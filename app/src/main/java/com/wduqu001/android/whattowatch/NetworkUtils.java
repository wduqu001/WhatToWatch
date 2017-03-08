package com.wduqu001.android.whattowatch;

import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class NetworkUtils {

    private final static String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w185";
    private final static String TMDB_POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TMDB_DISCOVER_POPULAR_URL = "http://api.themoviedb.org/3/discover/movie";
    private final static String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private final static String LANGUAGE = Locale.getDefault().getLanguage();
    private final static int PAGES = 1;

    // TODO: Add method description
    static URL buildPopularMoviesUrl(boolean descendingOrder) throws MalformedURLException {
        String order = "popularity.desc";

        if (!descendingOrder) order = "popularity.asc";

        Uri builtUri = Uri.parse(TMDB_DISCOVER_POPULAR_URL).buildUpon()
                .appendQueryParameter("sort_by", order)
                .appendQueryParameter("api_key", TMDB_API_KEY)
                .appendQueryParameter("language", LANGUAGE)
                .appendQueryParameter("page", Integer.toString(PAGES))
                .build();

        return new URL(builtUri.toString());
    }

    // TODO: Add method description
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // TODO: Add method description
    static List<Movie> getMoviesList(String StringParam) throws JSONException {

        JSONArray moviesArray = new JSONObject(StringParam).getJSONArray("results");

        if (moviesArray == null) {
            return null;
        }
        List<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieData = moviesArray.getJSONObject(i);

            try {
                Movie movie = getMovieFromJson(movieData);
                movieList.add(i, movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieList;
    }

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
     * @return false if internet connection is not available
     */
    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("themoviedb.org", 80);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

}
