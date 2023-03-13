package com.example.musicmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFrag extends Fragment implements View.OnClickListener {

    private View mView;
    Button logout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFrag newInstance(String param1, String param2) {
        SettingsFrag fragment = new SettingsFrag();
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
        mView = inflater.inflate(R.layout.fragment_settings, container, false);
        logout = mView.findViewById(R.id.logoutBtn);
        logout.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View view) {
        if (view == logout) {
//            MusicPlayer.player.stop();
//            MusicPlayer.player.release();
            FirebaseAuth.getInstance().signOut();
            getActivity().deleteSharedPreferences("userInfo");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            Toast.makeText(getActivity(), "logged out", Toast.LENGTH_SHORT).show();
        }
    }
}