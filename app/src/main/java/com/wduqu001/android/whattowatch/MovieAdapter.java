package com.wduqu001.android.whattowatch;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ContentValues[] mMovies;
    private final MovieAdapterOnClickHandler mClickHandler;

    MovieAdapter(Activity activity) {
        mClickHandler = (MovieAdapterOnClickHandler) activity;
    }

    void setMovies(ContentValues[] contentValues) {
        this.mMovies = contentValues;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out.
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        ContentValues movie = mMovies[position];
        final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w300";
        Uri posterUri = Uri.parse(TMDB_IMG_URL + movie.getAsString(COLUMN_POSTER_PATH)).normalizeScheme();
        Picasso.with(holder.mMovieImageView.getContext())
                .load(posterUri)
                .error(R.drawable.imagenotfoundheight)
                .fit()
                .into(holder.mMovieImageView);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        if (mMovies == null || mMovies.length < 1) {
            return 0;
        }
        return mMovies.length;
    }

    interface MovieAdapterOnClickHandler {
        void onClick(ContentValues movie);
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_view_movie) ImageView mMovieImageView;
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
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMovies[adapterPosition]);
        }
    }
}
