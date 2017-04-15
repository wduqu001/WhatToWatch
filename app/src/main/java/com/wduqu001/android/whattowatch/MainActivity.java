package com.wduqu001.android.whattowatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final int TOP_RATED = 1;
    private final int POPULAR_MOVIES = 0;
    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    private int mOption;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));
        mRecyclerView.setAdapter(mMovieAdapter);

        // recovering the instance state
        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
            mOption = savedInstanceState.getInt("option");
            updateView(mMovieList);
        } else {
            mOption = POPULAR_MOVIES;
            loadMovieList();
        }
    }

    /**
     * Dynamically calculate the number of columns
     *
     * @return number of columns
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 360; // The most common width among android devices and it's twice the side of the movie poster
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showLoading(int visibility) {
        mLoadingIndicator.setVisibility(visibility);
    }

    /**
     * Builds url and executes QueryTask loading a list of movies from the TMDB api.
     */
    private void loadMovieList() {
        URL url = null;
        try {
            url = NetworkUtils.buildMoviesUrl(mOption);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorMessage();
        }
        showLoading(View.VISIBLE);
        new MovieQueryTask(new TaskCompleteListener()).execute(url);
    }

    /**
     * Updates the title to reflect the current user's choice
     */
    private void UpdateTitle() {
        if (mOption == POPULAR_MOVIES) {
            setTitle(getString(R.string.popular));
        } else {
            setTitle(getString(R.string.top_rated));
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mOption = (item.getItemId() == R.id.action_top_rated ? TOP_RATED : POPULAR_MOVIES);
        loadMovieList();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationActivity = MovieDetail.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private void updateView(List<Movie> movieList) {
        showLoading(View.INVISIBLE);
        if (movieList == null || movieList.isEmpty()) {
            showErrorMessage();
            return;
        }
        mMovieList = movieList;
        mMovieAdapter.setMovies(mMovieList);
        mMovieAdapter.notifyDataSetChanged();
        UpdateTitle();
        showMovieDataView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) mMovieList);
        outState.putInt("option", mOption);
        super.onSaveInstanceState(outState);
    }

    class TaskCompleteListener implements QueryTaskCompleteListener<List<Movie>> {
        /**
         * Invoked when the AsyncTask has completed its execution.
         *
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(List<Movie> result) {
            updateView(result);
        }
    }
}
