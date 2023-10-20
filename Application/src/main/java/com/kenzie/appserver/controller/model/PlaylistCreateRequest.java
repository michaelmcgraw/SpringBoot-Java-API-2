package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class PlaylistCreateRequest {

    @NotEmpty
    @JsonProperty("playlistName")
    private String playlistName;

    @NotEmpty
    @JsonProperty("songs")
    private List<String> songs;

    @NotEmpty
    @JsonProperty("username")
    private String username;


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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "PlaylistCreateRequest{" +
                "playlistName='" + playlistName + '\'' +
                ", songs=" + songs +
                ", username='" + username + '\'' +
                '}';
    }
}
