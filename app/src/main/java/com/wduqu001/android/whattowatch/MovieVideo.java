package com.wduqu001.android.whattowatch;

public class MovieVideo {

    private String mKey;
    private String mName;

    public MovieVideo(String key, String name) {
        this.mKey = key;
        this.mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

}