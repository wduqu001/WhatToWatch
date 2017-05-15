package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.wduqu001.android.whattowatch.data.MoviesContract;
import com.wduqu001.android.whattowatch.sync.MovieQueryTask;
import com.wduqu001.android.whattowatch.sync.QueryTaskCompleteListener;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
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

        LoaderCallbacks<Cursor> callback = MainActivity.this;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(LOADER_ID, null, callback);

        mMovieAdapter = new MovieAdapter(this, this);
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
        // TODO: Load favorite movie list from database
        URL url = NetworkUtils.buildMoviesUrl(option);
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

    //    @Override
//    public void onClick(Movie movie) {
//        Context context = this;
//        Class destinationActivity = MovieDetail.class;
//
//        Intent intent = new Intent(context, destinationActivity);
//        intent.putExtra("movie", movie);
//        startActivity(intent);
//    }
    @Override
    public void onClick(int movieId) {
        Context context = this;
        Class destinationActivity = MovieDetail.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == LOADER_ID) {
            Uri queryUri = MoviesContract.MoviesEntry.CONTENT_URI;
            String sortOrder = "";
            String selection = "";

            return new CursorLoader(this,
                    queryUri,
                    null,
                    selection,
                    null,
                    sortOrder
            );
        } else {
            throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public class TaskCompleteListener implements QueryTaskCompleteListener<ContentValues[]> {
        /**
         * Invoked when the AsyncTask has completed its execution.
         *
         * @param values The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(ContentValues[] values) {
            showLoading(View.INVISIBLE);
            if (values == null || values.length < 1) {
                showErrorMessage();
                return;
            }
            mMovieAdapter.setMovies(values);
            mMovieAdapter.notifyDataSetChanged();
            showMovieDataView();
        }
    }

}
