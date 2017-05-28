package com.wduqu001.android.whattowatch.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.wduqu001.android.whattowatch.data.MoviesContract;

public class AsyncDbTasks extends AsyncTask<ContentValues, Void, Uri> {
    private final QueryTaskCompleteListener<Uri> mTaskCompleteListener;
    private final Context mContext;

    public AsyncDbTasks(QueryTaskCompleteListener<Uri> listener, Context context) {
        this.mTaskCompleteListener = listener;
        this.mContext = context;
    }

    @Override
    protected Uri doInBackground(ContentValues... contentValues) {
        return mContext.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues[0]);
    }

    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        mTaskCompleteListener.onTaskComplete(uri);
    }
}
