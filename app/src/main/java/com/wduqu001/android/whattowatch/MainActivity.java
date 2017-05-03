package com.wduqu001.android.whattowatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private final int MOST_POPULAR = 0;
    private final int TOP_RATED = 1;
    private final int FAVORITES = 2;

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieList(MOST_POPULAR);
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
     *
     * @param option Choose from one of the available options. (TOP_RATED, MOST_POPULAR)
     *               default: MOST_POPULAR
     */
    private void loadMovieList(int option) {
        URL url = null;
        try {
            url = NetworkUtils.buildMoviesUrl(option);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorMessage();
        }
        showLoading(View.VISIBLE);
        new MovieQueryTask(new TaskCompleteListener()).execute(url);
        UpdateTitle(option);
    }

    /**
     * Updates the title to reflect the current user's choice
     */
    private void UpdateTitle(int option) {
        if (option == MOST_POPULAR) {
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
        int id = item.getItemId();

        if (id < 0) {
            return false;
        }
        switch (id) {
            case R.id.action_top_rated:
                loadMovieList(TOP_RATED);
                break;

            case R.id.action_favorite:
                loadMovieList(FAVORITES);
                break;

            case R.id.action_most_popular:
                loadMovieList(MOST_POPULAR);
                break;
        }
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

    class TaskCompleteListener implements QueryTaskCompleteListener<List<Movie>> {
        /**
         * Invoked when the AsyncTask has completed its execution.
         *
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(List<Movie> result) {
            showLoading(View.INVISIBLE);
            if (result == null || result.isEmpty()) {
                showErrorMessage();
                return;
            }
            mMovieAdapter.setMovies(result);
            mMovieAdapter.notifyDataSetChanged();
            showMovieDataView();
        }
    }

}
