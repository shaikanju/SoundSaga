package com.anju.soundsaga;

import java.io.Serializable;
import java.util.List;

public class AudioBook  implements Serializable {
    String title;
    String author;
    String date;
    String language;
    String duration;
    String image;
    List<Chapter> contents;


    public AudioBook(String title, String author, String date, String language,
                     String duration, String image, List<Chapter> contents) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.language = language;
        this.duration = duration;
        this.image = image;
        this.contents = contents;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getLanguage() { return language; }
    public String getDuration() { return duration; }
    public String getImage() { return image; }
    public List<Chapter> getContents() { return contents; }
}
