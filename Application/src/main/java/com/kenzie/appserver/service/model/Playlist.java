package com.kenzie.appserver.service.model;

import java.util.List;
import java.util.Objects;

public class Playlist {
    private final String playlistId;
    private final String playlistName;
    private final List<String> songs;

    private final List<String> userIdList;
    private final String SpotifyPlaylistURI;

    public Playlist(String playlistId, String playlistName, List<String> songs, List<String> userIdList, String spotifyPlaylistURI){
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songs = songs;
        this.userIdList = userIdList;
        this.SpotifyPlaylistURI = spotifyPlaylistURI;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public List<String> getSongs() {
        return songs;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public String getSpotifyPlaylistURI() {
        return SpotifyPlaylistURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(playlistId, playlist.playlistId) && Objects.equals(playlistName, playlist.playlistName) && Objects.equals(songs, playlist.songs) && Objects.equals(userIdList, playlist.userIdList) && Objects.equals(SpotifyPlaylistURI, playlist.SpotifyPlaylistURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, playlistName, songs, userIdList, SpotifyPlaylistURI);
    }
}
