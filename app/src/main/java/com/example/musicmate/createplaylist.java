package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class createplaylist extends AppCompatActivity implements View.OnClickListener {
    ImageView coverImgPreview;
    ActionBar actionBar;
    EditText name, author;
    Button coverImg, createPlaylist;
    Playlist playlist;
    FirebaseAuth firebaseAuth;
    DatabaseReference UserRef, PlaylistRef;
    FirebaseDatabase firebaseDatabase;

    StorageReference storageReference;
    Uri imageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createplaylist);
        actionBar = getSupportActionBar();
        actionBar.hide();

        name = findViewById(R.id.nameEt);
        author = findViewById(R.id.authorEt);

        coverImgPreview = findViewById(R.id.coverImgPreview);

        coverImg = findViewById(R.id.coverimgBtn);
        coverImg.setOnClickListener(this);
        createPlaylist = findViewById(R.id.createPlaylistBtn);
        createPlaylist.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
        PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null & data.getData() != null) {
            imageURI = data.getData();
            coverImgPreview.setImageURI(imageURI);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == coverImg) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);
            // get image location and save its location probably in a variable(probably need permission).
        }
        if (view == createPlaylist) {
            if (!TextUtils.isEmpty(name.getText().toString()) || !TextUtils.isEmpty(author.getText().toString()))
                if (imageURI == null) {
                    //image doesn't exist
                    SavePlaylistToDatabase(false);
                    return;
                } else {
                    SavePlaylistToDatabase(true);
                    //image exists
                    return;
                }
        }
    }

    public void SavePlaylistToDatabase(Boolean withImage) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String playlistID = UUID.randomUUID().toString();

        playlist = new Playlist(uid, playlistID, name.getText().toString(), author.getText().toString(), null, null);
        if (imageURI != null) {
            playlist.setCoverImg("/playlistCoverImg/" + playlist.getUserID() + "/" + playlist.getPlaylistID());
            storageReference = FirebaseStorage.getInstance().getReference("playlistCoverImg/" + playlist.getUserID() + "/" + playlist.getPlaylistID());
            storageReference.putFile(imageURI);
        }
        PlaylistRef.child(uid).child(playlistID).setValue(playlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(createplaylist.this, "Created playlist", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(createplaylist.this, "Failed to create playlist", Toast.LENGTH_SHORT).show();
                FirebaseStorage.getInstance().getReference("playlistCoverImg/" + playlist.getPlaylistID()).delete();
                finish();
            }
        });
    }

}