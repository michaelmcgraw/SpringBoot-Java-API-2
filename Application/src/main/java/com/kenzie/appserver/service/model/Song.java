package com.kenzie.appserver.service.model;

public class Song {
    private final String songId;
    private final String songName;
    private final String artist;

    private final String SpotifyURI;

    public Song(String songId, String songName, String artist, String SpotifyURI){
        this.songId=songId;
        this.songName = songName;
        this.artist = artist;
        this.SpotifyURI = SpotifyURI;
    }

    public String getSongId() {
        return songId;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }
}
