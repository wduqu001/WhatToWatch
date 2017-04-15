package com.wduqu001.android.whattowatch;

class MovieReview {

    private String id;
    private String author;
    private String content;
    private String url;

    public MovieReview(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
