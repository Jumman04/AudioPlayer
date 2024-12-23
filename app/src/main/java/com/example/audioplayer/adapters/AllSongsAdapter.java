package com.example.audioplayer.adapters;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audioplayer.PlayerActivity;
import com.example.audioplayer.R;
import com.example.audioplayer.holders.AllSongsHolder;
import com.example.audioplayer.model.AllMusicFiles;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsHolder> {


    private final List<AllMusicFiles> allSongsList;


    public AllSongsAdapter(List<AllMusicFiles> allSongsList) {
        this.allSongsList = allSongsList;
    }

    @NonNull
    @Override
    public AllSongsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AllSongsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllSongsHolder holder, int position) {

        holder.player_song_name.setText(allSongsList.get(position).getSongName());
        holder.player_artist_name.setText(allSongsList.get(position).getArtistName());

        Context context = holder.itemView.getContext();
        byte[] image = getAlbumArt(context, allSongsList.get(position).getPath());

        if (image != null) Glide.with(context).asBitmap().load(image).into(holder.song_image);
        else Glide.with(context).load(R.drawable.demo).into(holder.song_image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("All", "all_music");
            context.startActivity(intent);

        });

        holder.three_dots.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.three_dots);
            popupMenu.getMenuInflater().inflate(R.menu.menu_more, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete) {
                    deleteFile(position, v);
                }
                return true;
            });


        });

    }

    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(allSongsList.get(position).getId()));
        File file = new File(allSongsList.get(position).getPath());
        boolean deleted = file.delete();

        if (deleted) {
            v.getContext().getContentResolver().delete(contentUri, null, null);
            allSongsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, allSongsList.size());
            Snackbar.make(v, "Song Deleted", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(v, "Can't Delete", Snackbar.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return allSongsList.size();
    }

    private byte[] getAlbumArt(Context context, String uri) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(context, Uri.parse(uri));
            return retriever.getEmbeddedPicture();
        } catch (IOException e) {
            return null;
        }
    }


}
