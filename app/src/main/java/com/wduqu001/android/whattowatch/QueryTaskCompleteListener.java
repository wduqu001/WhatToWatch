package com.wduqu001.android.whattowatch;

/**
 * Created by willian on 3/8/17.
 */

public interface QueryTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     *
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);
}
