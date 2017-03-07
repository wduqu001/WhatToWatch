package com.wduqu001.android.whattowatch;

import android.content.res.Configuration;
import android.os.AsyncTask;
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

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        mMovieAdapter = new MovieAdapter(MainActivity.this);
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

    /**
     * Builds url and executes QueryTask loading a list of movies from the TMDB api.
     * @param order Choose from one of the available sort options. (Ascending, Descending)
       default: DESCENDING
     */
    private void loadPopularMovieList(boolean order) {
        URL url = NetworkUtils.buildPopularMoviesUrl(order);
        new QueryTask().execute(url);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        // return true;
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

    private class QueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         */
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String moviesApiResult = null;
            try {
                moviesApiResult = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return moviesApiResult;
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}.
         * The specified result is the value returned by {@link #doInBackground}.
         */
        @Override
        protected void onPostExecute(String moviesApiResult) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesApiResult != null && !moviesApiResult.equals("")) {
                showMovieDataView();
                try {
                    List<Movie> mMovies = NetworkUtils.getMoviesList(moviesApiResult);
                    mMovieAdapter.setmMovies(mMovies);
                    mMovieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showErrorMessage();
            }
        }
    }
}
