package com.example.musicmate;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Playlist implements Serializable {
    private String userID;
    private String playlistID;
    private String name;
    private String author;
    private String coverImg;
    private ArrayList<String> songs;

    public Playlist() {
    }

    public Playlist(String userID,String playlistID, String name, String author, String coverImg, ArrayList<String> songs) {
        this.userID = userID;
        this.playlistID = playlistID;
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

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
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
        if(songs == null){
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(ArrayList<String> songs) {
        this.songs = songs;
    }

    public boolean addSong(String id) {
        if(songs == null){
            songs = new ArrayList<>();
        }
        if (songs.contains(id)) {
            return false;
        }
        songs.add(id);
        return true;
    }

    public boolean deleteSong(String id) {
        if (!songs.contains(id)) {
            return false;
        }
        songs.remove(id);
        return true;
    }
}
