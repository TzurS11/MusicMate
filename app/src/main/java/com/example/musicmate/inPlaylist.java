package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

        songsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = (Song) songsLV.getItemAtPosition(position);
                Toast.makeText(inPlaylist.this, song.getArtist(), Toast.LENGTH_SHORT).show();
            }
        });

        playPlaylist.setOnClickListener(this);

//        Toast.makeText(this, songs.toString(), Toast.LENGTH_SHORT).show();

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
                        song.setDownloadUrl("https://www.youtube.com/watch?v=" + song.getid());
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
                MusicPlayer.playFromUrlFromQueue(getApplicationContext());
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