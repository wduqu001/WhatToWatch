package com.wduqu001.android.whattowatch;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mMovies;
    private Context mContext;
    private MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
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
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/w185";
        Uri posterUri = Uri.parse(TMDB_IMG_URL + movie.getmPosterPath()).normalizeScheme();
            Picasso.with(holder.mMovieImageView.getContext())
                    .load(posterUri)
                    .fit()
                    .into(holder.mMovieImageView);
        Log.d(TAG, "help");
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        if(mMovies == null){
            return  0;
        }
        return mMovies.size();
    }

    // TODO: change it, sending the movieData instead of the ImageView
    public interface MovieAdapterOnClickHandler {
        void onClick(View view, int position);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.img_view_movie);
            //view.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(view, adapterPosition);
        }

    }
}
