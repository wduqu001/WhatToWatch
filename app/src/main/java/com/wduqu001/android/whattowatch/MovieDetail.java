package com.wduqu001.android.whattowatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity {

    private final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";

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

        Movie movie = getIntent().getParcelableExtra("movie");
        Picasso.with(this)
                .load(TMDB_IMG_URL + movie.getBackdropPath())
                .placeholder(R.drawable.placeholder350)
                .error(R.drawable.imagenotfound)
                .noFade()
                .fit()
                .into(mThumbnail);

        setTitle(movie.getTitle());

        mTitle.setText(movie.getOriginalTitle());
        if (!movie.getOverview().isEmpty()) mOverview.setText(movie.getOverview());
        String year;
        year = movie.getRelease_date() == null || movie.getRelease_date().isEmpty() ? "" : String.format("( %s )", movie.getRelease_date().substring(0, 4));
        mYearOfRelease.setText(year);
        mRating.setRating((float) movie.getVote_average() / 2);

    }
}
