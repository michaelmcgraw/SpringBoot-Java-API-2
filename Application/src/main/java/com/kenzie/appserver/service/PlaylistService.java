package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.PlaylistRepository;
import com.kenzie.appserver.repositories.model.PlaylistRecord;
import com.kenzie.appserver.service.model.Playlist;
import com.kenzie.appserver.service.model.User;
import com.kenzie.capstone.service.client.LambdaServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaylistService {
    private PlaylistRepository playlistRepository;
    private UserService userService;

    public PlaylistService(PlaylistRepository playlistRepository, UserService userService){
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    public Playlist findByPlaylistId(String playlistId){
        Playlist playlistFromRepository = playlistRepository
                .findById(playlistId)
                .map(playlistRecord -> new Playlist(playlistRecord.getPlaylistId(),playlistRecord.getPlaylistName(),playlistRecord.getSongs(), playlistRecord.getUserIdList(),playlistRecord.getSpotifyPlaylistURI()))
                .orElse(null);

        return playlistFromRepository;
    }

    public List<Playlist> getAllPlaylists() {
        Iterable<PlaylistRecord> playlistRecords = playlistRepository.findAll();
        List<Playlist> playlists = new ArrayList<>();

        for (PlaylistRecord playlistRecord : playlistRecords) {
            playlists.add(new Playlist(
                    playlistRecord.getPlaylistId(),
                    playlistRecord.getPlaylistName(),
                    playlistRecord.getSongs(),
                    playlistRecord.getUserIdList(),
                    playlistRecord.getSpotifyPlaylistURI()
            ));
        }

        return playlists;
    }

    public List<Playlist> getAllPlaylistsByUserId(String userId) {
        Iterable<PlaylistRecord> playlistRecords = playlistRepository.findAll();
        List<Playlist> playlists = new ArrayList<>();

        for (PlaylistRecord playlistRecord : playlistRecords) {
            playlists.add(new Playlist(
                    playlistRecord.getPlaylistId(),
                    playlistRecord.getPlaylistName(),
                    playlistRecord.getSongs(),
                    playlistRecord.getUserIdList(),
                    playlistRecord.getSpotifyPlaylistURI()
            ));
        }

        List<Playlist> filteredPlaylists = new ArrayList<>();

        for (Playlist playlist : playlists){
            if (playlist.getUserIdList().contains(userId)){
                filteredPlaylists.add(playlist);
            }
        }

        return filteredPlaylists;
    }
    

    public Playlist addSongToPlaylist(String playlistId, String song){
        Playlist existingPlaylist = findByPlaylistId(playlistId);
        List<String> songs = existingPlaylist.getSongs();
        songs.add(song);

        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setPlaylistId(existingPlaylist.getPlaylistId());
        playlistRecord.setPlaylistName(existingPlaylist.getPlaylistName());
        playlistRecord.setSongs(songs);
        playlistRecord.setUserIdList(existingPlaylist.getUserIdList());
        playlistRecord.setSpotifyPlaylistURI(existingPlaylist.getSpotifyPlaylistURI());

        playlistRepository.save(playlistRecord);

        return existingPlaylist;
    }

    //Updated to work
    public void addUserToPlaylist(String playlistId, String username) throws Exception {
        // Initialize AWS SNS client
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        Playlist playlistToUpdate = findByPlaylistId(playlistId);
        System.out.println(playlistToUpdate.toString());

        List<String> userIds = playlistToUpdate.getUserIdList();
        userIds.add(user.getUserId());

        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setPlaylistId(playlistToUpdate.getPlaylistId());
        playlistRecord.setPlaylistName(playlistToUpdate.getPlaylistName());
        playlistRecord.setSongs(playlistToUpdate.getSongs());
        playlistRecord.setUserIdList(userIds);
        playlistRecord.setSpotifyPlaylistURI(playlistToUpdate.getSpotifyPlaylistURI());
        playlistRepository.save(playlistRecord);

        // Publish to SNS topic
        String topicArn = "arn:aws:sns:us-east-1:653914639905:NotifyUser";
        String message = "User " + username + " added to playlist " + playlistId;

        PublishRequest publishRequest = new PublishRequest(topicArn, message);
        snsClient.publish(publishRequest);

        userService.addPlaylistToUser(username, playlistId);
    }

    public Playlist addNewPlaylist(Playlist playlist){
        PlaylistRecord playlistRecord = new PlaylistRecord();

        playlistRecord.setPlaylistId(playlist.getPlaylistId());
        playlistRecord.setPlaylistName(playlist.getPlaylistName());
        playlistRecord.setSongs(playlist.getSongs());
        playlistRecord.setUserIdList(playlist.getUserIdList());
        playlistRecord.setSpotifyPlaylistURI(playlist.getSpotifyPlaylistURI());

        System.out.println(playlistRecord.getUserIdList().get(0));
        playlistRepository.save(playlistRecord);
        return playlist;
    }

    public void updatePlaylist(Playlist playlist){
        PlaylistRecord existingPlaylistRecord = playlistRepository.findById(playlist.getPlaylistId()).orElse(null);

        if (existingPlaylistRecord != null){
            existingPlaylistRecord.setPlaylistId(playlist.getPlaylistId());
            existingPlaylistRecord.setPlaylistName(playlist.getPlaylistName());
            existingPlaylistRecord.setSongs(playlist.getSongs());
            existingPlaylistRecord.setSpotifyPlaylistURI(playlist.getSpotifyPlaylistURI());
            playlistRepository.save(existingPlaylistRecord);
        }
    }
}
