package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE;

public class MovieDetail extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.img_thumbnail) ImageView mThumbnail;
    @BindView(R.id.tv_title) TextView mTitle;
    @BindView(R.id.tv_yearOfRelease) TextView mYearOfRelease;
    @BindView(R.id.rb_voteAverage) RatingBar mRating;
    @BindView(R.id.tv_overview) TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        ContentValues movie = getIntent().getParcelableExtra("movie");
        String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";
        Picasso.with(this)
                .load(TMDB_IMG_URL + movie.getAsString(COLUMN_BACKDROP_PATH))
                .placeholder(R.drawable.placeholder350)
                .error(R.drawable.imagenotfound)
                .noFade()
                .fit()
                .into(mThumbnail);

        setTitle(movie.getAsString(COLUMN_TITLE));

        mTitle.setText(movie.getAsString(COLUMN_ORIGINAL_TITLE));
        mOverview.setText(movie.getAsString(COLUMN_OVERVIEW));
        String date = movie.getAsString(COLUMN_RELEASE_DATE);
        String year = date == null || date.isEmpty() ? "" : String.format("( %s )", date.substring(0, 4));
        mYearOfRelease.setText(year);
        mRating.setRating((float) ( movie.getAsDouble(COLUMN_VOTE_AVERAGE) / 2));

    }
}
