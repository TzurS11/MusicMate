package com.example.musicmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFrag extends Fragment implements View.OnClickListener {

    private View mView;
    Button createPlaylist;
    ListView playlists;

    ArrayList<Playlist> uploadsPlaylists;
    AllPlaylistsAdapter adapter;
    Playlist playlist;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference PlaylistRef;

    private ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaylistsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistsFrag newInstance(String param1, String param2) {
        PlaylistsFrag fragment = new PlaylistsFrag();
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
        mView = inflater.inflate(R.layout.fragment_playlists, container, false);

        createPlaylist = mView.findViewById(R.id.createPlaylistBtn);
        createPlaylist.setOnClickListener(this);
        playlists = mView.findViewById(R.id.playlists);
        retriveData();
        playlists.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Playlist playlist = (Playlist) playlists.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(),inPlaylist.class);
                intent.putExtra("playlist",playlist);
//                intent.putExtra("playlistID",playlist.getPlaylistID());
                startActivity(intent);
            }
        });

        return mView;
    }

    public void retriveData() {
        uploadsPlaylists = new ArrayList<>();
        if (adapter != null) adapter.clear();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PlaylistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(uid);
        PlaylistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (adapter != null) adapter.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Playlist upload = postSnapshot.getValue(Playlist.class);
                    uploadsPlaylists.add(upload);
                }
                if (uploadsPlaylists.size() >= 8) {
                    playlists.setStackFromBottom(false);
                } else {
                    playlists.setStackFromBottom(true);
                }
                adapter = new AllPlaylistsAdapter(mView.getContext().getApplicationContext(), 1, uploadsPlaylists);
                playlists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == createPlaylist) {
            Intent intent = new Intent(getActivity(), createplaylist.class);
            startActivity(intent);
            if (adapter != null)
                adapter.clear();
            return;
        }
    }
}