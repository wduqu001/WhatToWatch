package com.wduqu001.android.whattowatch.sync;

public interface QueryTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     *
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);
}
