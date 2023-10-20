package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.PlaylistCreateRequest;
import com.kenzie.appserver.controller.model.PlaylistResponse;
import com.kenzie.appserver.service.CachingPlaylistService;
import com.kenzie.appserver.service.PlaylistService;
import com.kenzie.appserver.service.model.Playlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlists")
@CrossOrigin(origins = "*")
public class PlaylistController {

    private CachingPlaylistService playlistService;

    PlaylistController(CachingPlaylistService playlistService){this.playlistService = playlistService;}

    //Reachable, logic needs to be fixed.  It is not adding a playlistId to the User
    @PostMapping
    public ResponseEntity<PlaylistResponse> addPlaylist(@RequestBody PlaylistCreateRequest playlistCreateRequest) {
        String playlistId = UUID.randomUUID().toString();
        List<String> userIds = new ArrayList<>();
        userIds.add("temp");

        Playlist playlist = new Playlist(
                playlistId,
                playlistCreateRequest.getPlaylistName(),
                playlistCreateRequest.getSongs(),
                userIds,
                "temp"
        );
        System.out.println("made it here");
        playlistService.addNewPlaylist(playlist);
        try {
            playlistService.addUserToPlaylist(playlistId, playlistCreateRequest.getUsername());
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
        PlaylistResponse playlistResponse = createPlaylistResponse(playlist);
        return ResponseEntity.created(URI.create("/playlists/" + playlistResponse.getPlaylistId())).body(playlistResponse);
    }

    //working
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable ("playlistId") String playlistId){
        Playlist playlist =playlistService.findByPlaylistId(playlistId);
        if (playlist == null){
            return ResponseEntity.notFound().build();
        }
        PlaylistResponse playlistResponse = createPlaylistResponse(playlist);
        return ResponseEntity.ok(playlistResponse);
    }

    //Working
    @GetMapping()
    public ResponseEntity<List<PlaylistResponse>> getAllPlaylistsByUserId(@RequestParam String userId) {
        List<Playlist> playlists = playlistService.getAllPlaylistsByUserId(userId);

        if (playlists.isEmpty()) {
            System.out.println("No playlists found");
            return ResponseEntity.notFound().build();
        }

        List<PlaylistResponse> playlistResponses = playlists.stream()
                .map(this::createPlaylistResponse)
                .collect(Collectors.toList());

        System.out.println("Fetched " + playlistResponses.size() + " playlists for user ID: " + userId);
        return ResponseEntity.ok(playlistResponses);
    }

    //This works
    @PutMapping("/{playlistId}/add-song")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(
            @PathVariable("playlistId") String playlistId,
            @RequestParam String songRequest){

        playlistService.addSongToPlaylist(playlistId, songRequest);

        return ResponseEntity.ok().build();
    }

    //Updated but still need to work on
    @PostMapping("/{playlistId}/users/{username}/add")
    public ResponseEntity<PlaylistResponse> addUserToPlaylist(
            @PathVariable("playlistId") String playlistId,
            @PathVariable("username") String username
            ) {
        try {
            playlistService.addUserToPlaylist(playlistId, username);
            Playlist updatedPlaylist = playlistService.findByPlaylistId(playlistId);

            if (updatedPlaylist == null) {
                return ResponseEntity.notFound().build();
            }

            PlaylistResponse playlistResponse = createPlaylistResponse(updatedPlaylist);

            return ResponseEntity.ok(playlistResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    private PlaylistResponse createPlaylistResponse(Playlist playlist){

    PlaylistResponse playlistResponse = new PlaylistResponse();

    playlistResponse.setPlaylistId(playlist.getPlaylistId());
    playlistResponse.setPlaylistName(playlist.getPlaylistName());
    playlistResponse.setSongs(playlist.getSongs());
    playlistResponse.setUserIds(playlist.getUserIdList());
    playlistResponse.setSpotifyPlaylistURI(playlist.getSpotifyPlaylistURI());

    return playlistResponse;

    }
}
