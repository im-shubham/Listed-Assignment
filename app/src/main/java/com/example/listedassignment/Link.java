package com.example.listedassignment;

public class Link {

    private String title;
    private String url;
    private String thumbnail;

    public Link(String title, String url, String thumbnail) {
        this.title = title;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
