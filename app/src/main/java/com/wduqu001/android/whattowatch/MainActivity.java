package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
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

import com.wduqu001.android.whattowatch.sync.MovieQueryTask;
import com.wduqu001.android.whattowatch.sync.QueryTaskCompleteListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.POPULAR;
import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.TOP_RATED;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    private String mOption;
    private MovieAdapter mMovieAdapter;
    private ContentValues[] mMovieListContent;
    public final static String FAVORITES = "favorites";

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
            mMovieListContent = (ContentValues[]) savedInstanceState.getParcelableArray(
                    getString(R.string.movie_list_content));
            mOption = savedInstanceState.getString("option");
            updateView(mMovieListContent);
        } else {
            mOption = POPULAR;
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
        showLoading(View.VISIBLE);
        new MovieQueryTask(new TaskCompleteListener(), this).execute(mOption);
    }

    /**
     * Updates the title to reflect the current user's choice
     */
    private void UpdateTitle() {
        if (mOption.equals(POPULAR)) {
            setTitle(getString(R.string.popular));
        }
        else if(mOption.equals(TOP_RATED)) {
            setTitle(getString(R.string.top_rated));
        } else {
            setTitle(getString(R.string.favorites));
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
        switch (item.getItemId()){
            case R.id.action_top_rated:
                mOption = TOP_RATED;
                break;
            case R.id.action_favorite:
                mOption = FAVORITES;
                break;
            default:
                mOption = POPULAR;
        }
        loadMovieList();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(ContentValues movieContent) {
        Context context = this;
        Class destinationActivity = MovieDetail.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(getString(R.string.movie_content), movieContent);
        startActivity(intent);
    }

    private void updateView(ContentValues[] contentValues) {
        showLoading(View.INVISIBLE);
        if (contentValues == null || contentValues.length < 1) {
            showErrorMessage();
            return;
        }
        mMovieListContent = contentValues;
        mMovieAdapter.setMovies(mMovieListContent);
        mMovieAdapter.notifyDataSetChanged();
        UpdateTitle();
        showMovieDataView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(getString(R.string.movie_list_content), mMovieListContent);
        outState.putString("option", mOption);
        super.onSaveInstanceState(outState);
    }

    private class TaskCompleteListener implements QueryTaskCompleteListener<ContentValues[]> {
        /**
         * Invoked when the AsyncTask has completed its execution.
         *
         * @param result The resulting object from the AsyncTask.
         */
        @Override
        public void onTaskComplete(ContentValues[] result) {
            updateView(result);
        }
    }
}
