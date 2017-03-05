package com.wduqu001.android.whattowatch;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by willian on 2/27/17.
 */

public class Movie  implements Parcelable{
    private static final String TAG = Movie.class.getSimpleName();

    private String mMoviedId;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;

    public Movie(String moviedId, String title, String posterPath, String backdropPath) {
        this.mMoviedId = moviedId;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
    }

    public Movie(Parcel in) {
        this.mMoviedId = in.readString();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mBackdropPath = in.readString();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
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
        dest.writeString(this.mMoviedId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPosterPath);
        dest.writeString(this.mBackdropPath);
    }

    public String getmMoviedId() {
        return mMoviedId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public  final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
