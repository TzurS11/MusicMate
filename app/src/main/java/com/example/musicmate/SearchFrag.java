package com.example.musicmate;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import org.w3c.dom.Text;

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
    public static Double similarityValue = 0.35;
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

    public void closeKeyboard(EditText editText) {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        search = mView.findViewById(R.id.searchIV);
        query = mView.findViewById(R.id.queryET);

        songs = mView.findViewById(R.id.songsLV);

        query.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    closeKeyboard(query);
                    retriveData();
                    return true;
                }
                return false;
            }
        });
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
        ArrayList<String> alreadyIn = new ArrayList<>();
        if (adapter != null) adapter.clear();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SongRef = FirebaseDatabase.getInstance().getReference("songs");
//        Query querySeach = SongRef.orderByChild("name")
//                .startAt(query.getText().toString().toUpperCase())
//                .endAt(query.getText().toString().toLowerCase() + "\uf8ff");
        SongRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.wtf("tag",String.valueOf(similarity("our painted sky", "our painted sky")));

                if (adapter != null) adapter.clear();
                songs.setVisibility(View.VISIBLE);

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Song upload = postSnapshot.getValue(Song.class);

                    if (query.getText().toString().trim().equals("")) {
                        if (uploadsSongs.size() < 20 && Math.round(Math.random() * 20) == 10) {
                            uploadsSongs.add(upload);
                        }
                    } else {
//                        Log.wtf("tag", upload.getname().toLowerCase() + " + " + query.getText().toString().toLowerCase() + " = " + similarity(upload.getname().toLowerCase(), query.getText().toString().toLowerCase()));
                        Log.wtf("tag",similarityValue.toString());
                        if (similarity(upload.getname().toLowerCase().trim(), query.getText().toString().toLowerCase().trim()) >= similarityValue || similarity(upload.getArtist().toLowerCase().trim(), query.getText().toString().toLowerCase().trim()) >= similarityValue && !alreadyIn.contains(upload.getid())) {
                            uploadsSongs.add(upload);
                            alreadyIn.add(upload.getid());
                        }
//                        String[] querySplit = query.getText().toString().split(" ");
//                        for (int i = 0; i < querySplit.length; i++) {
//                            if ((upload.getname().toLowerCase().contains(querySplit[i].toLowerCase()) || upload.getArtist().toLowerCase().contains(querySplit[i].toLowerCase())) && !alreadyIn.contains(upload.getid())) {
//                                uploadsSongs.add(upload);
//                                alreadyIn.add(upload.getid());
//                            }
//                        }
                    }

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
        TextView songTitlePreview = dialog.findViewById(R.id.songNamePreview);
        TextView songArtistPreview = dialog.findViewById(R.id.songArtistPreview);
        songTitlePreview.setText(song.getname());
        songTitlePreview.setSelected(true);
        songArtistPreview.setText(song.getArtist());
        songArtistPreview.setSelected(true);

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


    private static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings have zero length */
        }
        return (longerLength - getLevenshteinDistance(longer, shorter)) / (double) longerLength;
    }


    private static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

}