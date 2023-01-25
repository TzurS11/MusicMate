package com.example.musicmate;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFrag extends Fragment implements View.OnClickListener {

    private View mView;
    Button play, pause;
    TextView duration;
    String length;

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
        play = mView.findViewById(R.id.playBtn);
        play.setOnClickListener(this);
        pause = mView.findViewById(R.id.pauseBtn);
        pause.setOnClickListener(this);

        play.setClickable(false);
        play.setEnabled(false);
        pause.setClickable(false);
        pause.setEnabled(false);
        duration = mView.findViewById(R.id.duration);

        if (!((afterlogin) getActivity()).datasourceExist) {

            try {
                ((afterlogin) getActivity()).mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/musicmate-619b1.appspot.com/o/songs%2Fhappy.flac?alt=media&token=e5dc4c67-1108-423c-856b-795b4b425482");
            } catch (IOException e) {
//            throw new RuntimeException(e);
            }
            ((afterlogin) getActivity()).mediaPlayer.prepareAsync();

            ((afterlogin) getActivity()).mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    play.setClickable(true);
                    play.setEnabled(true);
                    pause.setClickable(true);
                    pause.setEnabled(true);


                    float millis = mediaPlayer.getDuration();
                    int minutes = (int) ((millis / 1000) / 60);
                    int seconds = (int) ((millis / 1000) % 60);
                    duration.setText(minutes + ":" + seconds);
                    length = duration.getText().toString();


                }
            });
            ((afterlogin) getActivity()).datasourceExist = true;
        } else {
            play.setClickable(true);
            play.setEnabled(true);
            pause.setClickable(true);
            pause.setEnabled(true);


            float millis = ((afterlogin) getActivity()).mediaPlayer.getDuration();
            int minutes = (int) ((millis / 1000) / 60);
            int seconds = (int) ((millis / 1000) % 60);
            duration.setText(minutes + ":" + seconds);
            length = duration.getText().toString();
        }


        return mView;
    }

    @Override
    public void onClick(View view) {
        if (view == play) {
            if (((afterlogin) getActivity()).mediaPlayer.isPlaying()) {
                ((afterlogin) getActivity()).mediaPlayer.seekTo(0);
                return;
            }
            ((afterlogin) getActivity()).mediaPlayer.start();
        }
        if (view == pause) {
            ((afterlogin) getActivity()).mediaPlayer.pause();
        }
    }


}