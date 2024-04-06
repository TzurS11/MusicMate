package com.example.musicmate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFrag extends Fragment {
    private View mView;
    EditText query;

    ListView songs;


    ArrayList<Song> uploadsSongs;
    AllSongsAdapter adapter;
    AllPlaylistsAdapter playlistsAdapter;



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
                .debounce(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
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

        String URL = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=" + query.getText().toString() + "&type=video&key=APIKEY";
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

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                adapter = new AllSongsAdapter(mView.getContext().getApplicationContext(), 1, uploadsSongs);
                songs.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error in fetching api", Toast.LENGTH_SHORT).show();
            }
        });

        if(afterlogin.currentScreen == "search"){
            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(jsonObjectRequest);
        }
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


}

