package com.wduqu001.android.whattowatch;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
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
        loadMovieList();
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // TODO: Implement a way to choose how the movie list is sorted
    private void loadMovieList() {
        boolean sortByPopularity = true;
        URL url = NetworkUtils.buildUrl(sortByPopularity);
        new QueryTask().execute(url);
    }

    private class QueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

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
