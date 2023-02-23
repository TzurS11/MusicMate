package com.example.musicmate;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;

public class MusicPlayer extends AppCompatActivity {
    public static ExoPlayer player;

    public MusicPlayer() {
        player = new ExoPlayer.Builder(getApplicationContext()).build();
    }
}
