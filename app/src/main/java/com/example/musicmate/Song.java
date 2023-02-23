package com.example.musicmate;

import java.io.Serializable;

public class Song implements Serializable {
    private String id;
    private String name;
    private String artist;
    private String coverImg;
    private String downloadUrl;

    public Song() {
    }

    public Song(String id, String name, String artist, String coverImg, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.coverImg = coverImg;
        this.downloadUrl = downloadUrl;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
