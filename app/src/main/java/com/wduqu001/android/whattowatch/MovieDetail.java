package com.wduqu001.android.whattowatch;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

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
    @BindView(R.id.cl_detailsView)
    ConstraintLayout mConstraintDetailsView;
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
    @BindView(R.id.action_play_trailer)
    Button mPlayTrailer;
    @BindView(R.id.pb_loading_indicator_details)
    ProgressBar mLoadingIndicator;
    private static ContentValues mMovieContent;
    private static ContentValues[] mReviewsContent;
    private static ContentValues[] mVideosContent;
    private static String mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mMovieContent = getIntent().getParcelableExtra("movie");
        if(mMovieContent != null && mMovieContent.size() > 2){
            loadMovieContent(mMovieContent);
        }
        // TODO: if mMovieContent <= 2, query movie from API, use movieId stored in DB
    }

    private void loadMovieContent(ContentValues movieContent) {
        mConstraintDetailsView.setVisibility(View.INVISIBLE);
        showLoading(View.VISIBLE);

        mMovieId = mMovieContent.getAsString(COLUMN_MOVIE_ID);

        String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";
        Picasso.with(this)
                .load(TMDB_IMG_URL + movieContent.getAsString(COLUMN_BACKDROP_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mThumbnailImageView);

        Picasso.with(this)
                .load(TMDB_IMG_URL + mMovieContent.getAsString(COLUMN_POSTER_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mPosterImageView);

        mTitleTextView.setText(movieContent.getAsString(COLUMN_ORIGINAL_TITLE));
        mOverviewTextView.setText(movieContent.getAsString(COLUMN_OVERVIEW));
        mRatingRatingBar.setRating((float) (movieContent.getAsDouble(COLUMN_VOTE_AVERAGE) / 2));

        String date = movieContent.getAsString(COLUMN_RELEASE_DATE);
        String year = date == null || date.isEmpty() ? "" : String.format("( %s )", date.substring(0, 4));
        mYearOfReleaseTextView.setText(year);

        new MovieQueryTask(new TaskCompleteListener(NetworkUtils.VIDEOS))
                .execute(NetworkUtils.VIDEOS, mMovieId);
        new MovieQueryTask(new TaskCompleteListener(NetworkUtils.REVIEWS))
                .execute(NetworkUtils.REVIEWS, mMovieId);
    }

    private void updateView(ContentValues[] contentValues, String contentType) {
        showLoading(View.INVISIBLE);
        if (contentValues == null || contentValues.length < 1) {
            Toast.makeText(this, "Unable to load content. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (contentType){
            case NetworkUtils.VIDEOS:
                mVideosContent = contentValues;
                mPlayTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        watchYoutubeVideo(mVideosContent[0].getAsString("key"));
                    }
                });
                break;
            case NetworkUtils.REVIEWS:
                mReviewsContent = contentValues;
                // TODO: MISSING REVIEWS IMPLEMENTATION
                break;
        }
        mConstraintDetailsView.setVisibility(View.VISIBLE);
    }

    public void watchYoutubeVideo(String key){
        String youtube_url = "https://www.youtube.com/watch?v=";
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtube_url + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void showLoading(int visibility) {
        mLoadingIndicator.setVisibility(visibility);
    }

    // TODO: Store selected movie on database
    public void addToWatchList(View view) {
        String posterPath = mMovieContent.getAsString(COLUMN_POSTER_PATH);
        Toast.makeText(this, mMovieContent.getAsString(COLUMN_TITLE) + mMovieId, Toast.LENGTH_SHORT).show();
    }

    private class TaskCompleteListener implements QueryTaskCompleteListener<ContentValues[]> {
        String mContent;
         TaskCompleteListener(String option) {
             mContent = option;
        }

        @Override
        public void onTaskComplete(ContentValues[] result) {
            updateView(result, mContent);
        }
    }
}
