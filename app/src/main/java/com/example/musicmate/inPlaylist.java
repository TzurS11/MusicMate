package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class inPlaylist extends AppCompatActivity implements View.OnClickListener {

    ActionBar actionBar;
    TextView name, author;
    ListView songsLV;
    ImageView playPlaylist;
    DatabaseReference PlaylistRef;
    Playlist playlist;
    ImageView playlistBackgroundImg;
    ArrayList<String> songs = null;

    ArrayList<Song> uploadsSongs;
    AllSongsAdapter adapter;
    AllPlaylistsAdapter playlistsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_playlist);

        actionBar = getSupportActionBar();
        actionBar.hide();

        name = findViewById(R.id.playlistName);
        author = findViewById(R.id.playlistAuthor);
        playlistBackgroundImg = findViewById(R.id.coverImgPreview);
        songsLV = findViewById(R.id.songsLV);
        playPlaylist = findViewById(R.id.playSongButton);


        Intent intent = getIntent();
        playlist = (Playlist) intent.getExtras().getSerializable("playlist");
        songs = playlist.getSongs();
        setPlaylistInfo(playlist);

//        songsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Song song = (Song) songsLV.getItemAtPosition(position);
//                Toast.makeText(inPlaylist.this, song.getArtist(), Toast.LENGTH_SHORT).show();
//            }
//        });
        songsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) songsLV.getItemAtPosition(position);
                showLongClickDialog(song);
//                Toast.makeText(inPlaylist.this, song.getname(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        playPlaylist.setOnClickListener(this);

//        Toast.makeText(this, songs.toString(), Toast.LENGTH_SHORT).show();

    }


    public void showLongClickDialog(Song song) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.selectedsong);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout gotoSong = dialog.findViewById(R.id.gotoSongLayout);

        gotoSong.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(getResources().getColor(R.color.primary));
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(getResources().getColor(R.color.accent));
                        break;
                }
                return false;
            }
        });
        gotoSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    gotoSongDialog(song);
                } catch (ExtractionException e) {
                    throw new RuntimeException(e);
                } catch (YoutubeRequestException e) {
                    throw new RuntimeException(e);
                } catch (VideoIsUnavailable e) {
                    throw new RuntimeException(e);
                }
            }
        });

        LinearLayout removeSong = dialog.findViewById(R.id.removeSongLayout);
        removeSong.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(getResources().getColor(R.color.primary));
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(getResources().getColor(R.color.accent));
                        break;
                }
                return false;
            }
        });

        removeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlist.deleteSong(song.getid());
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                PlaylistRef = FirebaseDatabase
                        .getInstance()
                        .getReference("Playlist")
                        .child(uid)
                        .child(playlist.getPlaylistID());
                PlaylistRef.setValue(playlist);
                dialog.dismiss();
                retriveData();
                Toast.makeText(inPlaylist.this, "Removed song", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }


    public void gotoSongDialog(Song song) throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...Please Wait");
        progressDialog.show();
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.addsongdialog);
        ListView playlists = dialog.findViewById(R.id.playlistSelect);

        playlists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Playlist playlist = (Playlist) playlists.getItemAtPosition(position);
                if (!playlist.addSong(song.getid())) {
                    Toast.makeText(inPlaylist.this, "Song already exists in the playlist", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist");
                    PlaylistRef.child(playlist.getUserID()).child(playlist.getPlaylistID()).setValue(playlist);
                    dialog.dismiss();
                }
            }
        });


        ArrayList<Playlist> uploadsPlaylists = new ArrayList<>();
        if (playlistsAdapter != null) playlistsAdapter.clear();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(uid);
        PlaylistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (playlistsAdapter != null) playlistsAdapter.clear();
                playlists.setVisibility(View.VISIBLE);

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Playlist upload = postSnapshot.getValue(Playlist.class);
                    if (!upload.getSongs().contains(song.getid())) {
                        uploadsPlaylists.add(upload);
                    }
                }
                if (uploadsPlaylists.size() >= 8) {
                    playlists.setStackFromBottom(false);
                } else {
                    playlists.setStackFromBottom(true);
                }
                playlistsAdapter = new AllPlaylistsAdapter(getApplicationContext(), 1, uploadsPlaylists);
                playlists.setAdapter(playlistsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        ImageView songCoverPreview = dialog.findViewById(R.id.coverImgPreview);
        TextView songTitlePreview = dialog.findViewById(R.id.songNamePreview);
        TextView songArtistPreview = dialog.findViewById(R.id.songArtistPreview);
        songTitlePreview.setText(song.getname());
        songTitlePreview.setSelected(true);
        songArtistPreview.setText(song.getArtist());
        songArtistPreview.setSelected(true);


        if (song.getCoverImg() != null) {
            Picasso.get().load(song.getCoverImg()).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(songCoverPreview);
        } else {
            songCoverPreview.setImageResource(R.drawable.songplaceholder);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);


        ImageView playSong = dialog.findViewById(R.id.playSongButton);
        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.currentSong = song;
                try {
                    MusicPlayer.playFromUrlFromQueue();
                } catch (ExtractionException e) {
                    throw new RuntimeException(e);
                } catch (YoutubeRequestException e) {
                    throw new RuntimeException(e);
                } catch (VideoIsUnavailable e) {
                    throw new RuntimeException(e);
                }
                Log.wtf("tag", song.getDownloadUrl());
                dialog.dismiss();
            }
        });
        dialog.show();
        progressDialog.dismiss();
    }

    private void setPlaylistInfo(Playlist playlist) {
        name.setText(playlist.getName());
        author.setText(playlist.getAuthor());
        if (playlist.getCoverImg() != null) {
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
        retriveData();
    }

    public void retriveData() {
        uploadsSongs = new ArrayList<>();
        ArrayList<String> alreadyIn = new ArrayList<>();
        if (adapter != null) adapter.clear();

        for (int i = 0; i < songs.size(); i++) {


            String URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=" + songs.get(i) + "&key=AIzaSyDaey08lNnIvrWby7TaROcjJev3uj5OIXo";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                Toast.makeText(getActivity(), "success in fetching api", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject pageInfo = response.getJSONObject("pageInfo");
                        JSONArray items = response.getJSONArray("items");

                        Song song = new Song();

                        JSONObject video = items.getJSONObject(0);
                        JSONObject snippet = video.getJSONObject("snippet");
                        String id = video.getString("id");

                        song.setid(id);
                        song.setArtist(snippet.getString("channelTitle"));
                        song.setname(snippet.getString("title").trim());
                        song.setDownloadUrl(null);
                        song.setCoverImg(snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url"));


                        if (!alreadyIn.contains(song.getid())) {
                            alreadyIn.add(song.getid());
                            uploadsSongs.add(song);
                        }


//                    Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    adapter = new AllSongsAdapter(getApplicationContext(), 1, uploadsSongs);
                    songsLV.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        }


    }

    @Override
    public void onClick(View view) {
        if (view == playPlaylist) {
            MusicPlayer.setQueue(uploadsSongs);
            try {
                MusicPlayer.playFromUrlFromQueue();
            } catch (ExtractionException e) {
                throw new RuntimeException(e);
            } catch (YoutubeRequestException e) {
                throw new RuntimeException(e);
            } catch (VideoIsUnavailable e) {
                throw new RuntimeException(e);
            }
            finish();
        }
    }
}