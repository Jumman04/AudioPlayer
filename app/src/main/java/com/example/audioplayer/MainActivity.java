package com.example.audioplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audioplayer.adapters.AllSongsAdapter;
import com.example.audioplayer.model.AllMusicFiles;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView all_songs_recycler;

    AllSongsAdapter allSongsAdapter;

    PermissionHandler permissionHandler;

    public static boolean shuffleBoolean = false , repeatBoolean = false;

  public static List<AllMusicFiles> allSongs = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        all_songs_recycler = findViewById(R.id.all_songs_recycler);
        permissionHandler = new PermissionHandler(MainActivity.this);
        all_songs_recycler.setHasFixedSize(true);


        if (permissionHandler.checkStoragePermission()) {




            allSongsAdapter = new AllSongsAdapter(MainActivity.this, allSongs);
            all_songs_recycler.setAdapter(allSongsAdapter);
            all_songs_recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            getAllAudioFiles();

        } else {
            permissionHandler.requestPermissions();
        }








    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllAudioFiles() {
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {

                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA ,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                String song_album = cursor.getString(0);
                String song_name = cursor.getString(1);
                String song_duration = cursor.getString(2);
                String song_path = cursor.getString(3);
                String song_artist = cursor.getString(4);
                String song_id = cursor.getString(5);

                allSongs.add(new AllMusicFiles(song_path , song_name , song_album , song_artist , song_duration , song_id) );
            }
            cursor.close();
        }


        allSongsAdapter.notifyDataSetChanged();

    }


}