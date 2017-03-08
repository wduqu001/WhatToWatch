package com.wduqu001.android.whattowatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents some information collected about a Movie from the api.
 */

public class Movie implements Parcelable {

    private String mMovieId;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private double mVoteAverage;
    private String mOverview;
    private String mReleaseDate;
    private String mOriginalTitle;

    Movie(String moviedId, String title, String posterPath, String backdropPath) {
        this.mMovieId = moviedId;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
    }

    protected Movie(Parcel in) {
        mMovieId = in.readString();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mVoteAverage = in.readDouble();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mOriginalTitle = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mOriginalTitle);
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
