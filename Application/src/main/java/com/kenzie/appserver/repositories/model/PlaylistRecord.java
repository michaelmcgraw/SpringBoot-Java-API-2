package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.kenzie.appserver.service.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "Playlist")
public class PlaylistRecord {
    private String playlistId;
    private String playlistName;
    private List<String> songs;

    private List<String> userIdList;
    private String SpotifyPlaylistURI;


    @DynamoDBHashKey(attributeName = "PlaylistId")
    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    @DynamoDBAttribute(attributeName = "PlaylistName")
    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    @DynamoDBAttribute(attributeName = "Songs")
    public List<String> getSongs() {
        return songs==null?new ArrayList<>():songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    @DynamoDBAttribute(attributeName = "UserIds")
    public List<String> getUserIdList() {
        return userIdList==null?new ArrayList<>():userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    @DynamoDBAttribute(attributeName = "SpotifyPlaylistURI")
    public String getSpotifyPlaylistURI() {
        return SpotifyPlaylistURI;
    }
    public void setSpotifyPlaylistURI(String SpotifyPlaylistURI) {
        this.SpotifyPlaylistURI = SpotifyPlaylistURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistRecord that = (PlaylistRecord) o;
        return playlistId.equals(that.playlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId);
    }
}
