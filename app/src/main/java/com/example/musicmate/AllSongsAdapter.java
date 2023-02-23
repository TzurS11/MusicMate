package com.example.musicmate;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllSongsAdapter extends ArrayAdapter<Song> {
    Context context;
    List<Song> objects;

    public AllSongsAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.songitem, parent, false);
        TextView songName = view.findViewById(R.id.songname);
        TextView songArtist = view.findViewById(R.id.songartist);
        ImageView songCoverImage = view.findViewById(R.id.songimage);

        Song temp = objects.get(position);

        songName.setText(temp.getname());
        songArtist.setText(temp.getArtist());

        if (temp.getCoverImg() != null) {
            FirebaseStorage.getInstance().getReference(temp.getCoverImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(songCoverImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    songCoverImage.setImageResource(R.drawable.songplaceholder);
                }
            });

        }else{
            songCoverImage.setImageResource(R.drawable.songplaceholder);
        }


        return view;
    }
}
