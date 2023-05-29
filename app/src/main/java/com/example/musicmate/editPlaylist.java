package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class editPlaylist extends AppCompatActivity implements View.OnClickListener {

    ActionBar actionBar;
    Playlist playlist;
    ImageView coverImgPreview;
    EditText name, author;
    Button coverImg, savePlaylist;
    String coverImgUri = null;
    Uri imageURI = null;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        coverImgPreview = findViewById(R.id.coverImgPreview);
        name = findViewById(R.id.nameEt);
        author = findViewById(R.id.authorEt);
        savePlaylist = findViewById(R.id.savePlaylistBtn);
        savePlaylist.setOnClickListener(this);
        coverImg = findViewById(R.id.coverimgBtn);
        coverImg.setOnClickListener(this);


        playlist = (Playlist) intent.getExtras().getSerializable("playlist");


        name.setText(playlist.getName());
        author.setText(playlist.getAuthor());

        if (playlist.getCoverImg() != null) {
            FirebaseStorage.getInstance().getReference(playlist.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null) {
                        coverImgUri = uri.toString();
                        Picasso.get().load(uri).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(coverImgPreview);
                    } else {
                        coverImgPreview.setImageResource(R.drawable.songplaceholder);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    coverImgPreview.setImageResource(R.drawable.songplaceholder);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view == savePlaylist) {
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
        if (view == coverImg) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null & data.getData() != null) {
            imageURI = data.getData();
            coverImgPreview.setImageURI(imageURI);
        }
    }

    private void SavePlaylistToDatabase(Boolean withImage) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(uid).child(playlist.getPlaylistID());

        Playlist newPlaylist = new Playlist(playlist.getUserID(), playlist.getPlaylistID(), name.getText().toString(), author.getText().toString(), null, playlist.getSongs());
        if (imageURI != null) {
            playlist.setCoverImg("/playlistCoverImg/" + playlist.getUserID() + "/" + playlist.getPlaylistID());
            storageReference = FirebaseStorage.getInstance().getReference("playlistCoverImg/" + playlist.getUserID() + "/" + playlist.getPlaylistID());
            newPlaylist.setCoverImg("/playlistCoverImg/" + uid + "/" + newPlaylist.getPlaylistID());
            storageReference.putFile(imageURI);
        }
        PlaylistRef.setValue(newPlaylist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(editPlaylist.this, "Edited playlist", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editPlaylist.this, "Failed to edit playlist", Toast.LENGTH_SHORT).show();
                FirebaseStorage.getInstance().getReference("playlistCoverImg/" + playlist.getPlaylistID()).delete();
                finish();
            }
        });


    }
}