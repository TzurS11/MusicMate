package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class inPlaylist extends AppCompatActivity {

    ActionBar actionBar;
    TextView test;
    DatabaseReference PlaylistRef;
    Playlist playlist;
    ImageView playlistBackgroundImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_playlist);

        actionBar = getSupportActionBar();
        actionBar.hide();

        test = findViewById(R.id.playlistnametest);
        playlistBackgroundImg = findViewById(R.id.coverImgPreview);

        Intent intent = getIntent();
        playlist = (Playlist) intent.getExtras().getSerializable("playlist");
        setPlaylistInfo(playlist);

    }

    private void setPlaylistInfo(Playlist playlist) {
        test.setText(playlist.getName());
        if(playlist.getCoverImg() != null){
            FirebaseStorage.getInstance().getReference(playlist.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(playlistBackgroundImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    playlistBackgroundImg.setImageResource(R.drawable.songplaceholder);
                }
            });
        }
//        Log.wtf("tag",playlistid);
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(uid).child(playlistid);
//        PlaylistRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    playlist = snapshot.getValue(Playlist.class);
//                    test.setText(playlist.getName());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
}