package com.kenzie.appserver.service;

import com.google.gson.Gson;

import com.kenzie.appserver.cache.CacheClient;
import com.kenzie.appserver.repositories.PlaylistRepository;
import com.kenzie.appserver.service.model.Playlist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CachingPlaylistService {
    private PlaylistRepository playlistRepository;
    private CacheClient cacheClient;
    private UserService userService;
    private PlaylistService playlistService;
    private Gson gson;

    public CachingPlaylistService(
            PlaylistRepository playlistRepository,
            CacheClient cacheClient,
            UserService userService,
            PlaylistService playlistService
    ){
        this.playlistRepository = playlistRepository;
        this.cacheClient = cacheClient;
        this.userService = userService;
        this.playlistService = playlistService;
        this.gson = new Gson();
    }

    public Playlist findByPlaylistId(String playlistId){
        String json = cacheClient.getValue(playlistId);
        if (json != null) {
            Playlist playlist = gson.fromJson(json, Playlist.class);
            return playlist;
        }
        Playlist playlist = playlistService.findByPlaylistId(playlistId);
        cacheClient.setValue(playlistId, 300, gson.toJson(playlist));
        return playlist;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return playlists;
    }

    public List<Playlist> getAllPlaylistsByUserId(String userId) {
        return playlistService.getAllPlaylistsByUserId(userId);
    }
    

    public Playlist addSongToPlaylist(String playlistId, String song){
        cacheClient.deleteValue(playlistId);
        return playlistService.addSongToPlaylist(playlistId, song);
    }

    //Updated to work
    public void addUserToPlaylist(String playlistId, String username) throws Exception {
        cacheClient.deleteValue(playlistId);
        playlistService.addUserToPlaylist(playlistId, username);
    }

    public Playlist addNewPlaylist(Playlist playlist){
        return playlistService.addNewPlaylist(playlist);
    }

    public void updatePlaylist(Playlist playlist){
        playlistService.updatePlaylist(playlist);
    }
}
