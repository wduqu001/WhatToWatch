package com.wduqu001.android.whattowatch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    final boolean ASCENDING = false;
    final boolean DESCENDING = true;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        loadPopularMovieList(DESCENDING);
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
     * @param order Choose from one of the available sort options. (Ascending, Descending)
     *              default: DESCENDING
     */
    private void loadPopularMovieList(boolean order) {
        URL url = null;
        try {
            url = NetworkUtils.buildPopularMoviesUrl(order);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorMessage();
        }
        showLoading(View.VISIBLE);
        new MovieQueryTask(this, new TaskCompleteListener()).execute(url);
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

        if (id == R.id.action_sort_asc) {
            loadPopularMovieList(ASCENDING);
            return true;
        }
        if (id == R.id.action_sort_desc) {
            loadPopularMovieList(DESCENDING);
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
