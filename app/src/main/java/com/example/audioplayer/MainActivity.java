package com.example.audioplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audioplayer.adapters.AllSongsAdapter;
import com.example.audioplayer.model.AllMusicFiles;
import com.jummania.DataManager;
import com.jummania.DataManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static boolean shuffleBoolean = false, repeatBoolean = false;
    public static final List<AllMusicFiles> allSongs = new ArrayList<>();
    RecyclerView all_songs_recycler;
    PermissionHandler permissionHandler;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        all_songs_recycler = findViewById(R.id.all_songs_recycler);
        permissionHandler = new PermissionHandler(MainActivity.this);
        all_songs_recycler.setHasFixedSize(true);

        if (permissionHandler.checkStoragePermission()) getAllAudioFiles();
        else permissionHandler.requestPermissions();


    }


    private void getAllAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID};

        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String song_album = cursor.getString(0);
                    String song_name = cursor.getString(1);
                    String song_duration = cursor.getString(2);
                    String song_path = cursor.getString(3);
                    String song_artist = cursor.getString(4);
                    String song_id = cursor.getString(5);

                    allSongs.add(new AllMusicFiles(song_path, song_name, song_album, song_artist, song_duration, song_id));
                }
            }
        }


        all_songs_recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        all_songs_recycler.setAdapter(new AllSongsAdapter(allSongs));

        DataManager dataManager = DataManagerFactory.create(getFilesDir());
        dataManager.saveList("allSongs", allSongs);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) getAllAudioFiles();
            else permissionHandler.showDialog(permissions);
        }
    }
}