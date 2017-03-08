package com.wduqu001.android.whattowatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView mThumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        TextView mYearOfRelease = (TextView) findViewById(R.id.tv_yearOfRelease);
        RatingBar mRating = (RatingBar) findViewById(R.id.rb_voteAverage);
        TextView mOverview = (TextView) findViewById(R.id.tv_overview);

        Movie movie = getIntent().getParcelableExtra("movie");
            Picasso.with(this)
                    .load(TMDB_IMG_URL + movie.getBackdropPath())
                    .fit()
                    .placeholder(R.drawable.placeholder350)
                    .into(mThumbnail);

        setTitle(movie.getTitle());

        mTitle.setText(movie.getOriginalTitle());
        if(!movie.getOverview().isEmpty()) mOverview.setText(movie.getOverview());
        String year;
        year = movie.getRelease_date() == null || movie.getRelease_date().isEmpty() ? "" : String.format("( %s )", movie.getRelease_date().substring(0, 4));
        mYearOfRelease.setText(year);
        mRating.setRating((float) movie.getVote_average() / 2);

    }
}
