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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.Locale.getDefault;

class NetworkUtils {

    public static final String TAG = "NetworkUtils";
    private final static String TMDB_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String POPULAR_MOVIES = "popular";
    private final static String TOP_RATED = "top_rated";
    private final static String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private final static String LANGUAGE = getDefault().getLanguage().concat("-").concat(getDefault().getCountry());
    private final static int PAGES = 1;

    /**
     * * Builds a url for PopularMovies
     *
     * @param option Choose between "most popular movies(0)" and "top rated movies (1)"
     * @return a new url for the tmdb api
     * @throws MalformedURLException
     */
    static URL buildMoviesUrl(int option) throws MalformedURLException {
        String baseUrl = TMDB_MOVIE_URL;

        if (option == 1) baseUrl += TOP_RATED;
        else baseUrl += POPULAR_MOVIES;

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
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
        List<Movie> movieList;
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
        // TODO: Find a way to load the additionalMovieData without slowing down the app
        getMovieDetails(movieList.get(0));
        return movieList;
    }

    /**
     * Build the URL for a specific movie
     *
     * @param movieId The movie to be requested
     * @return URL
     * @throws MalformedURLException
     */
    private static URL buildMovieUrl(String movieId) throws MalformedURLException {
        String appendedQuerys = "reviews,videos";

        Uri builtUri = Uri.parse(TMDB_MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter("api_key", TMDB_API_KEY)
                .appendQueryParameter("append_to_response", appendedQuerys)
                .build();

        return new URL(builtUri.toString());
    }

    /**
     * Gets additional information about a movie. Currently it only gets videos and reviews
     *
     * @param movie the movie object to be updated
     */
    private static void getMovieDetails(Movie movie) {
        String movieId = movie.getMovieId();
        try {
            URL url = buildMovieUrl(movieId);
            String httpResult = getResponseFromHttpUrl(url);

            JSONArray videosArray = new JSONObject(httpResult).getJSONObject("videos").getJSONArray("results");
            JSONArray reviewsArray = new JSONObject(httpResult).getJSONObject("reviews").getJSONArray("results");
            movie.setVideos(getVideos(videosArray));
            if (reviewsArray.length() > 0) {
                movie.setReviews(getReviews(reviewsArray));
                Log.d(TAG, "getMovieDetails: No reviews available");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<MovieVideo> getVideos(JSONArray videosArray) throws JSONException {
        ArrayList<MovieVideo> videos = new ArrayList<>();
        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject movieData = videosArray.getJSONObject(i);
            MovieVideo video = new MovieVideo(
                    movieData.getString("key"),
                    movieData.getString("name"));
            videos.add(video);
        }
        return videos;
    }

    private static ArrayList<MovieReview> getReviews(JSONArray videosArray) throws JSONException {
        ArrayList<MovieReview> reviews = new ArrayList<>();
        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject movieData = videosArray.getJSONObject(i);
            Log.i(TAG, "getMovieDetails: getReviews: " + movieData);
            MovieReview review = new MovieReview(
                    movieData.getString("id"),
                    movieData.getString("author"),
                    movieData.getString("content"),
                    movieData.getString("url")
            );
            reviews.add(review);
        }
        return reviews;
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
