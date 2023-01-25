package com.example.musicmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class createplaylist extends AppCompatActivity implements View.OnClickListener {

    EditText name, author;
    Button coverImg, createPlaylist;
    Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createplaylist);


        name = findViewById(R.id.nameEt);
        author = findViewById(R.id.authorEt);

        coverImg = findViewById(R.id.coverimgBtn);
        coverImg.setOnClickListener(this);
        createPlaylist = findViewById(R.id.createPlaylistBtn);
        createPlaylist.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == coverImg) {
            // get image location and save its location probably in a variable(probably need permission).
        }
        if (view == createPlaylist) {
            // upload data to firebase (check if the user has uploaded an image and if not use a default playlist image).

            playlist = new Playlist(name.getText().toString(), author.getText().toString(), "", "");
            String playlistString = playlist.toString();

        }
    }
}