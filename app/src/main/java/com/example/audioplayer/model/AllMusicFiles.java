package com.example.audioplayer.model;


import androidx.annotation.Keep;

@Keep
public class AllMusicFiles {
    final String path;
    final String songName;
    final String albumName;
    final String artistName;
    final String duration;
    final String id;


    public AllMusicFiles(String path, String songName, String albumName, String artistName, String duration, String id) {
        this.path = path;
        this.songName = songName;
        this.albumName = albumName;
        this.artistName = artistName;
        this.duration = duration;
        this.id = id;
    }


    public String getPath() {
        return path;
    }

    public String getSongName() {
        return songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }
}
