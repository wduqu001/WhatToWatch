package com.wduqu001.android.whattowatch;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wduqu001.android.whattowatch.sync.AsyncDbTasks;
import com.wduqu001.android.whattowatch.sync.MovieQueryTask;
import com.wduqu001.android.whattowatch.sync.QueryTaskCompleteListener;
import com.wduqu001.android.whattowatch.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_AVERAGE_RANKING;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.utilities.NetworkUtils.TMDB_IMG_URL;

public class MovieDetail extends AppCompatActivity {

    private static ContentValues mMovieContent;
    private static ContentValues[] mReviewsContent;
    private static ContentValues[] mVideosContent;
    private static String mMovieId;
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
    Button mPlayTrailerButton;
    @BindView(R.id.action_favorite)
    ImageButton mFavoriteImageButton;
    @BindView(R.id.pb_loading_indicator_details)
    ProgressBar mLoadingIndicator;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = MovieDetail.this;
        ButterKnife.bind(this);

        mMovieContent = getIntent().getParcelableExtra(getString(R.string.movie_content));
        boolean isFavorite = getIntent().getExtras().getBoolean(getString(R.string.favorites));

        loadMovieContent(mMovieContent);

        if (isFavorite) {
            updateFavoriteImageButton(getString(R.string.present), R.drawable.watchribbon_present);
        }
    }

    private void loadMovieContent(ContentValues movieContent) {
        mConstraintDetailsView.setVisibility(View.INVISIBLE);
        showLoading(View.VISIBLE);

        mMovieId = mMovieContent.getAsString(COLUMN_MOVIE_ID);

        Picasso.with(mContext)
                .load(TMDB_IMG_URL + movieContent.getAsString(COLUMN_BACKDROP_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mThumbnailImageView);

        Picasso.with(mContext)
                .load(TMDB_IMG_URL + mMovieContent.getAsString(COLUMN_POSTER_PATH))
                .error(R.drawable.imagenotfound)
                .fit()
                .into(mPosterImageView);

        mTitleTextView.setText(movieContent.getAsString(COLUMN_ORIGINAL_TITLE));
        setTitle(movieContent.getAsString(COLUMN_TITLE));
        mOverviewTextView.setText(movieContent.getAsString(COLUMN_OVERVIEW));
        mRatingRatingBar.setRating((float) (movieContent.getAsDouble(COLUMN_AVERAGE_RANKING) / 2));

        String date = movieContent.getAsString(COLUMN_RELEASE_DATE);
        String year = date == null || date.isEmpty() ? "" : String.format("( %s )", date.substring(0, 4));
        mYearOfReleaseTextView.setText(year);

        new MovieQueryTask(new TaskCompleteListener(NetworkUtils.VIDEOS), mContext)
                .execute(NetworkUtils.VIDEOS, mMovieId);
        new MovieQueryTask(new TaskCompleteListener(NetworkUtils.REVIEWS), mContext)
                .execute(NetworkUtils.REVIEWS, mMovieId);
    }

    private void updateView(ContentValues[] contentValues, String contentType) {
        showLoading(View.INVISIBLE);
        if (contentValues == null || contentValues.length < 1) {
            Toast.makeText(mContext, getString(R.string.unable_to_load), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (contentType) {
            case NetworkUtils.VIDEOS:
                mVideosContent = contentValues;
                mPlayTrailerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        watchYoutubeVideo(mVideosContent[0].getAsString("key"));
                    }
                });
                break;
            case NetworkUtils.REVIEWS:
                mReviewsContent = contentValues;
                mReviewTextView.setText("");
                mReviewTextView.append(mReviewsContent[0].getAsString("content"));
                break;
        }
        mConstraintDetailsView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a movie trailer on youtube
     *
     * @param key The key that identifies the video ( used by Youtube)
     */
    private void watchYoutubeVideo(String key) {
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

    public void updateFavorites(View view) {
        if (view.getTag(R.id.action_favorite) == getString(R.string.present)) {
            removeFromFavorites();
            updateFavoriteImageButton(getString(R.string.absent), R.drawable.watchribbon_absent);
            return;
        }
        addToFavorites();
        updateFavoriteImageButton(getString(R.string.present), R.drawable.watchribbon_present);
    }

    /**
     * Updates the favoriteImageButton properties.
     *
     * @param tag        A new TAG for the component
     * @param resourceId The id of the image resource to be used by the component
     */
    private void updateFavoriteImageButton(String tag, int resourceId) {
        mFavoriteImageButton.setTag(R.id.action_favorite, tag);
        mFavoriteImageButton.setImageResource(resourceId);
    }

    private void removeFromFavorites() {
        new AsyncDbTasks(new DbTaskCompleteListener()
                , mContext, null).execute(AsyncDbTasks.DELETE, mMovieId);
    }

    private void addToFavorites() {
        new AsyncDbTasks(new DbTaskCompleteListener()
                , mContext, mMovieContent).execute(AsyncDbTasks.INSERT);
    }

    private class TaskCompleteListener implements QueryTaskCompleteListener<ContentValues[]> {
        final String mContent;

        TaskCompleteListener(String option) {
            mContent = option;
        }

        @Override
        public void onTaskComplete(ContentValues[] result) {
            updateView(result, mContent);
        }
    }

    private class DbTaskCompleteListener implements QueryTaskCompleteListener<ContentValues> {

        @Override
        public void onTaskComplete(ContentValues result) {
            if (result == null) {
                Toast.makeText(mContext, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mContext, getString(R.string.favorite_list_updated), Toast.LENGTH_SHORT).show();
        }
    }
}
