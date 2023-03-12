package com.example.musicmate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFrag extends Fragment implements View.OnClickListener {
    Dialog playlistdialog = null;
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
                if (playlistdialog.isShowing()) return;
                Playlist playlist = (Playlist) playlists.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), inPlaylist.class);
                intent.putExtra("playlist", playlist);
//                intent.putExtra("playlistID",playlist.getPlaylistID());
                startActivity(intent);

            }
        });
        playlists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = (Playlist) playlists.getItemAtPosition(position);
                showLongClickDialog(playlist);
                return false;
            }
        });
        playlistdialog = new Dialog(requireActivity());

        return mView;
    }

    public void showLongClickDialog(Playlist playlist) {

        playlistdialog.setContentView(R.layout.selectedplaylsit);
        playlistdialog.getWindow().setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(playlistdialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        playlistdialog.getWindow().setAttributes(lp);
        playlistdialog.setCancelable(true);
        playlistdialog.setCanceledOnTouchOutside(true);

        LinearLayout editPlaylist = playlistdialog.findViewById(R.id.editPlaylistLayout);

        editPlaylist.setOnTouchListener(new View.OnTouchListener() {
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
        editPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistdialog.dismiss();
            }
        });

        LinearLayout deletePlaylist = playlistdialog.findViewById(R.id.deletePlaylistLayout);
        deletePlaylist.setOnTouchListener(new View.OnTouchListener() {
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

        deletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//


                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setCancelable(false);
                builder.setTitle("Delete");
                builder.setMessage("This action cannot be undone");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlaylistRef = FirebaseDatabase
                                        .getInstance()
                                        .getReference("Playlist")
                                        .child(uid)
                                        .child(playlist.getPlaylistID());
                                PlaylistRef.removeValue();
                                retriveData();
                                dialog.dismiss();
                                playlistdialog.dismiss();
                            }

                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        playlistdialog.dismiss();
                        return;
                    }
                });

                AlertDialog submitDialog = builder.create();
                submitDialog.show();
            }
        });
        playlistdialog.show();
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