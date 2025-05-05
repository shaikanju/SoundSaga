package com.anju.soundsaga;

import java.io.Serializable;

public class Chapter implements Serializable {
    private int number;
    private String title;
    private String url;

    // Constructor
    public Chapter(int number, String title, String url) {
        this.number = number;
        this.title = title;
        this.url = url;
    }

    // Getters and setters
    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
