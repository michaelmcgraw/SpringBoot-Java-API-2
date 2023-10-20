package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistResponse {

    @JsonProperty("playlistId")
    private String playlistId;

    @JsonProperty("playlistName")
    private String playlistName;

    @JsonProperty("songs")
    private List<String> songs;

    @JsonProperty("userIds")
    private List<String> userIds;
    @JsonProperty("spotifyPlaylistURI")
    private String spotifyPlaylistURI;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getSpotifyPlaylistURI() {
        return spotifyPlaylistURI;
    }

    public void setSpotifyPlaylistURI(String spotifyPlaylistURI) {
        spotifyPlaylistURI = spotifyPlaylistURI;
    }
}
