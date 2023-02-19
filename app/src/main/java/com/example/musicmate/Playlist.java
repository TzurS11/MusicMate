package com.example.musicmate;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Playlist implements Serializable {
    private String userID;
    private String name;
    private String author;
    private String coverImg;
    private ArrayList<String> songs;

    public Playlist(String userID, String name, String author, String coverImg, ArrayList<String> songs) {
        this.userID = userID;
        this.name = name;
        this.author = author;
        this.coverImg = coverImg;
        this.songs = songs;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<String> songs) {
        this.songs = songs;
    }

    public boolean addSong(String id) {
        if (songs.contains(id)) {
            return false;
        }
        songs.add(id);
        return true;
    }

    public boolean deleteSong(String id) {
        if (songs.contains(id)) {
            return false;
        }
        songs.remove(id);
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return "Playlist : " + "\n" +
                ", userID= " + this.userID + "\n" +
                ", name= " + this.name + "\n" +
                ", author= " + this.author + "\n" +
                ", coverImg= " + this.name + "\n" +
                ", songs= " + this.songs + "\n";
    }


}
