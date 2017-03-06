package com.wduqu001.android.whattowatch;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class NetworkUtils {

    private final static String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w185";
    private final static String TMDB_POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TMDB_POPULAR_ASC_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.asc";
    private final static String TMDB_API_KEY = "";
    private final static String LANGUAGE = Locale.getDefault().getLanguage();
    private final static int PAGES = 1;

    // TODO: Add sorting method
    public static URL buildUrl(boolean sortByPopularity) {
        Uri builtUri = Uri.parse(TMDB_POPULAR_MOVIES_URL).buildUpon()
                .appendQueryParameter("api_key", TMDB_API_KEY)
                .appendQueryParameter("language", LANGUAGE)
                .appendQueryParameter("page", Integer.toString(PAGES))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
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

    public static List<Movie> getMoviesList(String StringParam) throws JSONException {

        JSONArray moviesArray = new JSONObject(StringParam).getJSONArray("results");

        if (moviesArray == null) {
            return null;
        }
        List<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieData;
            movieData = moviesArray.getJSONObject(i);

            try {
                Movie movie = new Movie(
                        String.valueOf(movieData.getInt("id")),
                        movieData.getString("title"),
                        movieData.getString("poster_path"),
                        movieData.getString("backdrop_path")
                );
                movie.setOverview(movieData.getString("overview"));
                movie.setVote_average(movieData.getDouble("vote_average"));
                movie.setRelease_date(movieData.getString("release_date"));

                movieList.add(i, movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieList;
    }

}
