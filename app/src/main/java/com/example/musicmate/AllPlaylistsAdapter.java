package com.example.musicmate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AllPlaylistsAdapter extends ArrayAdapter<Playlist> {


    Context context;
    List<Playlist> objects;


    public AllPlaylistsAdapter(Context context, int resource, List<Playlist> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.playlistitem, parent, false);

        TextView playlistName = view.findViewById(R.id.playlistname);
        TextView playlistAuthor = view.findViewById(R.id.playlistauthor);
        ImageView playlistImage = view.findViewById(R.id.playlistimage);
        Playlist temp = objects.get(position);
        playlistName.setText(temp.getName());
        playlistAuthor.setText(temp.getAuthor());
        if (temp.getCoverImg() == null) {
            playlistImage.setImageResource(R.drawable.songplaceholder);
        } else {
            FirebaseStorage.getInstance().getReference(temp.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(playlistImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    playlistImage.setImageResource(R.drawable.songplaceholder);
                }
            });
        }
        return view;
    }
}
