package com.example.musicmate;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFrag extends Fragment {

    private View mView;
    ImageView search;
    EditText query;

    ListView songs;


    ArrayList<Song> uploadsSongs;
    AllSongsAdapter adapter;
    AllPlaylistsAdapter playlistsAdapter;
    Song song;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference SongRef;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFrag newInstance(String param1, String param2) {
        SearchFrag fragment = new SearchFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        search = mView.findViewById(R.id.searchIV);
        query = mView.findViewById(R.id.queryET);

        songs = mView.findViewById(R.id.songsLV);

        RxTextView.textChanges(query)
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(textChanged -> {
                    retriveData();
                });

        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = (Song) songs.getItemAtPosition(position);
                showDialog(song);
            }
        });

        return mView;
    }

    public void retriveData() {
        uploadsSongs = new ArrayList<>();
        if (adapter != null) adapter.clear();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SongRef = FirebaseDatabase.getInstance().getReference("songs");
        Query querySeach = SongRef.orderByChild("name")
                .startAt(query.getText().toString().toUpperCase())
                .endAt(query.getText().toString().toLowerCase() + "\uf8ff");
        querySeach.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (adapter != null) adapter.clear();
                songs.setVisibility(View.VISIBLE);

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Song upload = postSnapshot.getValue(Song.class);
                    uploadsSongs.add(upload);
                }
                if (uploadsSongs.size() >= 8) {
                    songs.setStackFromBottom(false);
                } else {
                    songs.setStackFromBottom(true);
                }

                adapter = new AllSongsAdapter(mView.getContext().getApplicationContext(), 1, uploadsSongs);
                songs.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showDialog(Song song) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.addsongdialog);
        ListView playlists = dialog.findViewById(R.id.playlistSelect);

        playlists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Playlist playlist = (Playlist) playlists.getItemAtPosition(position);
                if (!playlist.addSong(song.getid())) {
                    Toast.makeText(getActivity(), "Song already exists in the playlist", Toast.LENGTH_SHORT).show();
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
                playlistsAdapter = new AllPlaylistsAdapter(mView.getContext().getApplicationContext(), 1, uploadsPlaylists);
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

        if (song.getCoverImg() != null) {
            FirebaseStorage.getInstance().getReference(song.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(songCoverPreview);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    songCoverPreview.setImageResource(R.drawable.songplaceholder);
                }
            });
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

}