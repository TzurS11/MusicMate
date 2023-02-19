package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class createplaylist extends AppCompatActivity implements View.OnClickListener {

    ActionBar actionBar;
    EditText name, author;
    Button coverImg, createPlaylist;
    Playlist playlist;
    FirebaseAuth firebaseAuth;
    //    FirebaseDatabase ;
    DatabaseReference UserRef, PlaylistRef;
    FirebaseDatabase firebaseDatabase;
    String imgURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createplaylist);
        actionBar = getSupportActionBar();
        actionBar.hide();

        name = findViewById(R.id.nameEt);
        author = findViewById(R.id.authorEt);

        coverImg = findViewById(R.id.coverimgBtn);
        coverImg.setOnClickListener(this);
        createPlaylist = findViewById(R.id.createPlaylistBtn);
        createPlaylist.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        PlaylistRef = firebaseDatabase.getReference("Playlist");
    }

    @Override
    public void onClick(View view) {
        if (view == coverImg) {
            // get image location and save its location probably in a variable(probably need permission).
        }
        if (view == createPlaylist) {
            if(imgURI == null){
                //image doesn't exist
                SavePlaylistToDatabase(false);
                return;
            }else{
                SavePlaylistToDatabase(true);
                //image exists
                return;
            }
        }
    }

    public void SavePlaylistToDatabase(Boolean withImage) {
        //firebaseDatabase = FirebaseDatabase.getInstance();
        //PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist");
        firebaseAuth = firebaseAuth.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        playlist = new Playlist(uid, name.getText().toString(), author.getText().toString(), "hghg", new ArrayList<String>());
        PlaylistRef.child(uid).setValue(playlist);
    }
}