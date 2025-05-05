package com.anju.soundsaga;

import java.io.Serializable;
import java.util.List;

public class MyBook implements Serializable {
    private String title;
    private String author;
    private String image;
    private int position;
    private float speed;
    private List<Chapter> chapters;
    private String currentChapter;
    private String  currentPlayTime;
    private String totalDuration;
    private String lastPlayedDateTime;


    public MyBook(String title, String author, String image, List<Chapter> chapters,
                  String currentChapter,int position, String currentPlayTime, String totalDuration, String lastPlayedDateTime,float speed) {
        this.title = title;
        this.author = author;
        this.image = image;
        this.chapters = chapters;
        this.currentChapter = currentChapter;
        this.position=position;
        this.currentPlayTime = currentPlayTime;
        this.totalDuration = totalDuration;
        this.lastPlayedDateTime = lastPlayedDateTime;
        this.speed = speed;
    }
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }
    public float getSpeed() {
        return speed;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public String getCurrentChapter() {
        return currentChapter;
    }
    public int getPosition() {
        return position;
    }


    public String getCurrentPlayTime() {
        return currentPlayTime;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public String getLastPlayedDateTime() {
        return lastPlayedDateTime;
    }

    // Optional: ToString method for easier debugging and logging
    @Override
    public String toString() {
        return "MyBook{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", image='" + image + '\'' +
                ", chapters=" + chapters +
                ", currentChapter=" + currentChapter +
                ", currentPlayTime='" + currentPlayTime + '\'' +
                ", totalDuration='" + totalDuration + '\'' +
                ", lastPlayedDateTime='" + lastPlayedDateTime + '\'' +
                '}';
    }
}
