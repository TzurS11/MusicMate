package com.example.musicmate;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Playlist implements Serializable {
    private String name;
    private String author;
    private String coverImg;
    private String songs;

    public Playlist(String name, String author, String coverImg, String songs) {
        this.name = name;
        this.author = author;
        this.coverImg = coverImg;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getSongs() {
        return songs;
    }

    public void setSongs(String songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public String toString() {
        return "Playlist : " + "\n" +
                ", name= " + this.name + "\n" +
                ", author= " + this.author + "\n" +
                ", coverImg= " + this.name + "\n" +
                ", songs= " + this.songs + "\n";
    }
}
