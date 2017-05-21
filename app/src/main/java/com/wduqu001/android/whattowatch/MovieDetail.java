package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE;

public class MovieDetail extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.img_thumbnail)
    ImageView mThumbnailImageView;
    @BindView(R.id.img_poster)
    ImageView mPosterImageView;
    @BindView(R.id.tv_title)
    TextView mTitleTextView;
    @BindView(R.id.tv_yearOfRelease)
    TextView mYearOfReleaseTextView;
    @BindView(R.id.rb_voteAverage)
    RatingBar mRatingRatingBar;
    @BindView(R.id.tv_overview)
    TextView mOverviewTextView;
    @BindView(R.id.tv_review)
    TextView mReviewTextView;
    private static ContentValues mMovieContent;
    private static ContentValues[] mReviewsContent;
    private static ContentValues[] mVideosContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        loadMovieContent();
    }

    private void loadMovieContent() {
        mMovieContent = getIntent().getParcelableExtra("movie");
        String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";

        Picasso.with(this)
                .load(TMDB_IMG_URL + mMovieContent.getAsString(COLUMN_BACKDROP_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mThumbnailImageView);

        Picasso.with(this)
                .load(TMDB_IMG_URL + mMovieContent.getAsString(COLUMN_POSTER_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mPosterImageView);

        setTitle(mMovieContent.getAsString(COLUMN_TITLE));

        mTitleTextView.setText(mMovieContent.getAsString(COLUMN_ORIGINAL_TITLE));
        mOverviewTextView.setText(mMovieContent.getAsString(COLUMN_OVERVIEW));
        String date = mMovieContent.getAsString(COLUMN_RELEASE_DATE);
        String year = date == null || date.isEmpty() ? "" : String.format("( %s )", date.substring(0, 4));
        mYearOfReleaseTextView.setText(year);
        mRatingRatingBar.setRating((float) (mMovieContent.getAsDouble(COLUMN_VOTE_AVERAGE) / 2));
    }

    // TODO: Store selected movie on database
    public void addToWatchList(View view) {
        String movieId = mMovieContent.getAsString(COLUMN_MOVIE_ID);
        Toast.makeText(this, mMovieContent.getAsString(COLUMN_TITLE) + movieId, Toast.LENGTH_SHORT).show();
    }
}
