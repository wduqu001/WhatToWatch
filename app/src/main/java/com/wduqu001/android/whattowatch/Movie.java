package com.wduqu001.android.whattowatch;

/**
 * Created by willian on 2/27/17.
 */

public class Movie {
    private static final String TAG = Movie.class.getSimpleName();

    private String mMoviedId;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private double mVote_average;
    private String mOverview;
    private String mRelease_date;

    public Movie(String moviedId, String title, String posterPath, String backdropPath) {
        this.mMoviedId = moviedId;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
    }

    public String getMoviedId() {
        return mMoviedId;
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
        return mVote_average;
    }

    public void setVote_average(double vote_average) {
        this.mVote_average = vote_average;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public String getRelease_date() {
        return mRelease_date;
    }

    public void setRelease_date(String release_date) {
        this.mRelease_date = release_date;
    }
}
