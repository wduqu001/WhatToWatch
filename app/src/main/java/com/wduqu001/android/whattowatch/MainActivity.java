package com.wduqu001.android.whattowatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // TODO: fill the list of movies
        List<Movie> mMovies =  new ArrayList<>();
        mMovies.add(0,new Movie(String.valueOf(438026),
                "Sherlock: The Lying Detective",
                "/e8In8IS1fzCJ8OlISe4z9zk857B.jpg",
                "/5IDyICd5k1JQbAlhSMN64yU4Wkk.jpg"
        ));
        mMovies.add(1,new Movie(String.valueOf(324552),
                "John Wick: Chapter 2",
                "/xUidyvYFsbbuExifLkslpcd8SMc.jpg",
                "/4TBLjAhQe1zJfR3zdHMWTrwbdLd.jpg"
        ));

        mMovieAdapter = new MovieAdapter(this, mMovies);
        mRecyclerView.setAdapter(mMovieAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        showMovieDataView();
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
