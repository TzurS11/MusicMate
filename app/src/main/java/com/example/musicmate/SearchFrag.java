package com.example.musicmate;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.telecom.Call;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.AdaptiveVideoStream;
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.json.Json;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        search = mView.findViewById(R.id.searchIV);
        query = mView.findViewById(R.id.queryET);

        songs = mView.findViewById(R.id.songsLV);

        songs.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (songs.getLastVisiblePosition() - songs.getHeaderViewsCount() -
                        songs.getFooterViewsCount()) >= (adapter.getCount() - 1)) {

                    Toast.makeText(getActivity(), "Cant find what you are looking for? Search by url/id", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });




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
                try {
                    showDialog(song);
                } catch (ExtractionException e) {
//                    throw new RuntimeException(e);
                } catch (YoutubeRequestException e) {
//                    throw new RuntimeException(e);
                } catch (VideoIsUnavailable e) {
//                    throw new RuntimeException(e);
                }
            }
        });

        return mView;
    }

    public void retriveData() {
        uploadsSongs = new ArrayList<>();
        ArrayList<String> alreadyIn = new ArrayList<>();
        if (adapter != null) adapter.clear();

        if (query.getText().toString().trim().equals("")) return;

        String URL = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=" + query.getText().toString() + "&type=video&key=AIzaSyDaey08lNnIvrWby7TaROcjJev3uj5OIXo";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(getActivity(), "success in fetching api", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject pageInfo = response.getJSONObject("pageInfo");
                    JSONArray items = response.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        Song song = new Song();

                        JSONObject video = items.getJSONObject(i);
                        JSONObject snippet = video.getJSONObject("snippet");
                        JSONObject id = video.getJSONObject("id");

                        song.setid(id.getString("videoId"));
                        song.setArtist(snippet.getString("channelTitle"));
                        song.setname(snippet.getString("title").trim());
                        song.setDownloadUrl("https://www.youtube.com/watch?v=" + song.getid());
                        song.setCoverImg(snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url"));


                        if (!alreadyIn.contains(song.getid())) {
                            alreadyIn.add(song.getid());
                            uploadsSongs.add(song);
                        }


                    }


//                    Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(getActivity(), uploadsSongs.size() + "", Toast.LENGTH_SHORT).show();
                adapter = new AllSongsAdapter(mView.getContext().getApplicationContext(), 1, uploadsSongs);
                songs.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error in fetching api", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jsonObjectRequest);


        //
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        SongRef = FirebaseDatabase.getInstance().getReference("songs");
////        Query querySeach = SongRef.orderByChild("name")
////                .startAt(query.getText().toString().toUpperCase())
////                .endAt(query.getText().toString().toLowerCase() + "\uf8ff");
//        SongRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                Log.wtf("tag",String.valueOf(similarity("our painted sky", "our painted sky")));
//
//                if (adapter != null) adapter.clear();
//                songs.setVisibility(View.VISIBLE);
//
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    Song upload = postSnapshot.getValue(Song.class);
//
//                    if (query.getText().toString().trim().equals("")) {
//                        if (uploadsSongs.size() < 20 && Math.round(Math.random() * 20) == 10) {
//                            uploadsSongs.add(upload);
//                        }
//                    } else {
////                        Log.wtf("tag", upload.getname().toLowerCase() + " + " + query.getText().toString().toLowerCase() + " = " + similarity(upload.getname().toLowerCase(), query.getText().toString().toLowerCase()));
//                        Log.wtf("tag",similarityValue.toString());
//                        if (similarity(upload.getname().toLowerCase().trim(), query.getText().toString().toLowerCase().trim()) >= similarityValue || similarity(upload.getArtist().toLowerCase().trim(), query.getText().toString().toLowerCase().trim()) >= similarityValue && !alreadyIn.contains(upload.getid())) {
//                            uploadsSongs.add(upload);
//                            alreadyIn.add(upload.getid());
//                        }
////                        String[] querySplit = query.getText().toString().split(" ");
////                        for (int i = 0; i < querySplit.length; i++) {
////                            if ((upload.getname().toLowerCase().contains(querySplit[i].toLowerCase()) || upload.getArtist().toLowerCase().contains(querySplit[i].toLowerCase())) && !alreadyIn.contains(upload.getid())) {
////                                uploadsSongs.add(upload);
////                                alreadyIn.add(upload.getid());
////                            }
////                        }
//                    }
//
//                }
//

//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void showDialog(Song song) throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...Please Wait");
        progressDialog.show();
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

//        if (song.getCoverImg() != null) {
//            FirebaseStorage.getInstance().getReference(song.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.get().load(uri).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(songCoverPreview);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    songCoverPreview.setImageResource(R.drawable.songplaceholder);
//                }
//            });
//        }

        if (song.getCoverImg() != null) {
            Picasso.get().load(song.getCoverImg()).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(songCoverPreview);
        } else {
            songCoverPreview.setImageResource(R.drawable.songplaceholder);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("videoUrls", Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(song.getid())) {
            YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
            VideoPlayerConfig videoData = youtubeJExtractor.extract(song.getid());
            String dashManifest = videoData.getStreamingData().getAdaptiveAudioStreams().get(0).getUrl();
            song.setDownloadUrl(dashManifest);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(song.getid(), dashManifest);
            editor.commit();
        } else {
            String songLink = sharedPreferences.getString(song.getid(), null);
            Uri uri = Uri.parse(songLink);
            Integer expirationDate = Integer.valueOf(uri.getQueryParameter("expire"));
            Date d = new Date(expirationDate);
            if(d.after(Calendar.getInstance().getTime())){
                YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
                VideoPlayerConfig videoData = youtubeJExtractor.extract(song.getid());
                String dashManifest = videoData.getStreamingData().getAdaptiveAudioStreams().get(0).getUrl();
                song.setDownloadUrl(dashManifest);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(song.getid(), dashManifest);
                editor.commit();
            }else{
                song.setDownloadUrl(sharedPreferences.getString(song.getid(), null));
            }
        }


        ImageView playSong = dialog.findViewById(R.id.playSongButton);
        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.playAndOverride(MediaItem.fromUri(song.getDownloadUrl()), song);
                Log.wtf("tag",song.getDownloadUrl());
                dialog.dismiss();
            }
        });
        dialog.show();
        progressDialog.dismiss();
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

