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

    final int POPULAR_MOVIES = 0;
    final int TOP_RATED = 1;
    private MovieAdapter mMovieAdapter;

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieList(POPULAR_MOVIES);
    }

    /**
     * Dynamically calculate the number of columns
     *
     * @return number of columns
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 352;
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

    void showLoading(int visibility) {
        mLoadingIndicator.setVisibility(visibility);
    }

    /**
     * Builds url and executes QueryTask loading a list of movies from the TMDB api.
     *
     * @param option Choose from one of the available options. (TOP_RATED, POPULAR_MOVIES)
     *               default: POPULAR_MOVIES
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

        if (id == R.id.action_top_rated) {
            loadMovieList(TOP_RATED);
            return true;
        }
        if (id == R.id.action_most_popular) {
            loadMovieList(POPULAR_MOVIES);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, Movie movie) {
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
