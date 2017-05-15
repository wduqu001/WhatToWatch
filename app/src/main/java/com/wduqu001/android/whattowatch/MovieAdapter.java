package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wduqu001.android.whattowatch.data.MoviesContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    // TODO: CLEANUP
    private final Context mContext;
    private final MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    private ContentValues[] mMovies;

    MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out.
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.movies_item, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String posterPath = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
        final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w342";
        Uri posterUri = Uri.parse(TMDB_IMG_URL + posterPath).normalizeScheme();
        Picasso.with(holder.mMovieImageView.getContext())
                .load(posterUri)
                .placeholder(R.drawable.placeholder350)
                .error(R.drawable.imagenotfoundheight)
                .noFade()
                .fit()
                .into(holder.mMovieImageView);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Deprecated
    void setMovies(ContentValues[] values) {
        this.mMovies = values;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_view_movie)
        ImageView mMovieImageView;

        MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            mClickHandler.onClick(mCursor.getInt(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
        }
    }
}
