package com.wduqu001.android.whattowatch;

/**
 * Created by willian on 2/27/17.
 */

public class Movie {
    private static final String TAG = Movie.class.getSimpleName();

    private String mMovieId;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private double mVoteAverage;
    private String mOverview;
    private String mReleaseDate;
    private String mOriginalTitle;

    public Movie(String moviedId, String title, String posterPath, String backdropPath) {
        this.mMovieId = moviedId;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public double getVote_average() {
        return mVoteAverage;
    }

    public void setVote_average(double vote_average) {
        this.mVoteAverage = vote_average;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public String getRelease_date() {
        return mReleaseDate;
    }

    public void setRelease_date(String release_date) {
        this.mReleaseDate = release_date;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }
}
