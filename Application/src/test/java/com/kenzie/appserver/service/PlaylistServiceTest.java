package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.PlaylistRepository;
import com.kenzie.appserver.repositories.model.PlaylistRecord;
import com.kenzie.appserver.service.model.Playlist;
import com.kenzie.appserver.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserService userService;

    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        playlistRepository = mock(PlaylistRepository.class);
        userService = mock(UserService.class);
        playlistService = new PlaylistService(playlistRepository, userService);
    }

    @Test

    void testFindByPlaylistId() {
        // given
        String playlistId = "playlist-1";
        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setPlaylistId(playlistId);

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlistRecord));

        // WHEN
        Playlist result = playlistService.findByPlaylistId(playlistId);

        // THEN
        verify(playlistRepository).findById(playlistId);
        assertNotNull(result);
        assertEquals(playlistId, result.getPlaylistId());
    }

    @Test
    void testGetAllPlaylists() {
        // GIVEN
        List<PlaylistRecord> playlistRecords = new ArrayList<>();
        playlistRecords.add(new PlaylistRecord());
        playlistRecords.add(new PlaylistRecord());

        when(playlistRepository.findAll()).thenReturn(playlistRecords);

        // WHEN
        List<Playlist> playlists = playlistService.getAllPlaylists();

        // THEN
        verify(playlistRepository).findAll();
        assertEquals(2, playlists.size());
    }

    @Test
    void testAddSongToPlaylist() {
        // GIVEN
        String playlistId = "playlist-1";
        String song = "Song Title";
        Playlist existingPlaylist = new Playlist(playlistId, "Test Playlist", new ArrayList<>(), new ArrayList<>(), "spotify:playlist:12345");
        existingPlaylist.getSongs().add("Existing Song");

        PlaylistRecord existingPlaylistRecord = new PlaylistRecord();
        existingPlaylistRecord.setPlaylistId(existingPlaylist.getPlaylistId());
        existingPlaylistRecord.setPlaylistName(existingPlaylist.getPlaylistName());
        existingPlaylistRecord.setSongs(existingPlaylist.getSongs());
        existingPlaylistRecord.setUserIdList(existingPlaylist.getUserIdList());
        existingPlaylistRecord.setSpotifyPlaylistURI(existingPlaylist.getSpotifyPlaylistURI());

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(existingPlaylistRecord));
        when(playlistRepository.save(Mockito.any(PlaylistRecord.class))).thenReturn(existingPlaylistRecord);

        // WHEN
        Playlist result = playlistService.addSongToPlaylist(playlistId, song);

        // THEN
        verify(playlistRepository).findById(playlistId);
        verify(playlistRepository).save(Mockito.any(PlaylistRecord.class));
        assertNotNull(result);
        assertEquals(2, result.getSongs().size());
        assertTrue(result.getSongs().contains(song));
    }
    @Test

    void testAddUserToPlaylistUserNotFound() throws Exception {
        // GIVEN

        String playlistId = "playlist-1";
        String username = "user123";

        when(userService.findByUsername(username)).thenReturn(null);

        // WHEN AND THEN

        assertThrows(Exception.class, () -> playlistService.addUserToPlaylist(playlistId, username));
    }

    @Test
    void testPlaylistByUserID_PlaylistExists() {
        // GIVEN
        String playlist = "playlist";
        Playlist expectedPlaylist = new Playlist(
                "playlistId",
                "playlistName",
                Collections.singletonList("songs"),
                Collections.singletonList("userIdList"),
                "SpotifyPlaylistURI"
        );
        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setPlaylistId(expectedPlaylist.getPlaylistId());
        playlistRecord.setPlaylistName(expectedPlaylist.getPlaylistName());
        playlistRecord.setSongs(expectedPlaylist.getSongs());
        playlistRecord.setUserIdList(expectedPlaylist.getUserIdList());
        playlistRecord.setSpotifyPlaylistURI(expectedPlaylist.getSpotifyPlaylistURI());

        Mockito.when(playlistRepository.findById(playlist)).thenReturn(Optional.of(playlistRecord));
        // WHEN
        Playlist result = playlistService.findByPlaylistId(playlist);
        //THEN
        assertEquals(expectedPlaylist.getPlaylistId(), result.getPlaylistId());
        assertEquals(expectedPlaylist.getPlaylistName(), result.getPlaylistName());
        assertEquals(expectedPlaylist.getSongs(), result.getSongs());
        assertEquals(expectedPlaylist.getUserIdList(), result.getUserIdList());
        assertEquals(expectedPlaylist.getSpotifyPlaylistURI(), result.getSpotifyPlaylistURI());

    }
    @Test
    void testAddNewPlaylist() {
        // GIVEN
        Playlist newPlaylist = new Playlist(
                "playlistId",
                "playlistName",
                Collections.singletonList("songs"),
                Collections.singletonList("userIdList"),
                "SpotifyPlaylistURI"
        );
        // WHEN
        playlistService.addNewPlaylist(newPlaylist);

    }
    @Test
    void updatePlaylist_ExistingPlaylist_ShouldUpdate() {
        // Given
        Playlist existingPlaylist = new Playlist("123", "Test Playlist", new ArrayList<>(), new ArrayList<>(), "Spotify URI");

        when(playlistRepository.findById("123")).thenReturn(Optional.of(new PlaylistRecord()));

        // When
        playlistService.updatePlaylist(existingPlaylist);

        // Then
        verify(playlistRepository, times(1)).save(Matchers.any(PlaylistRecord.class));
    }

    @Test
    void updatePlaylist_NonExistingPlaylist_ShouldNotUpdate() {
        // Given
        Playlist nonExistingPlaylist = new Playlist("456", "Non-existing Playlist", new ArrayList<>(), new ArrayList<>(), "Spotify URI");

        when(playlistRepository.findById("456")).thenReturn(Optional.empty());

        // When
        playlistService.updatePlaylist(nonExistingPlaylist);

        // Then
        verify(playlistRepository, never()).save(Matchers.any(PlaylistRecord.class));
        // Assert
        Mockito.verify(playlistRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void testGetAllPlaylistsByUserID() {
        // GIVEN
        String userId = "userId";
        List<PlaylistRecord> playlistRecords = new ArrayList<>();
        // Add playlist records
        PlaylistRecord playlistRecord1 = new PlaylistRecord();
        PlaylistRecord playlistRecord2 = new PlaylistRecord();
        playlistRecords.add(playlistRecord1);
        playlistRecords.add(playlistRecord2);
        when(playlistRepository.findAll()).thenReturn(playlistRecords);


        // WHEN
        List<Playlist> playlists = playlistService.getAllPlaylistsByUserId(userId);

        // THEN
        verify(playlistRepository).findAll();
        assertEquals(2, playlistRecords.size());

    }


    @Test
    void testAddUserToPlaylist_UserExistsAndPlaylistExist(){
        //GIVEN
        String playlistId = "1234";
        String username = "username";
        User user = new User(
                "123",
                username,
                "password",
                "email",
                new ArrayList<>()
        );
        List<String> userIds = new ArrayList<>();
        userIds.add("garbage");
        PlaylistRecord playlistRecord = new PlaylistRecord();
        playlistRecord.setUserIdList(userIds);
        playlistRecord.setPlaylistName("name");
        playlistRecord.setSpotifyPlaylistURI("uri");
        playlistRecord.setSongs(userIds);
        playlistRecord.setPlaylistId("123");
        //WHEN
        when(userService.findByUsername(username)).thenReturn(user);
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlistRecord));

        //THEN
    }

    @Test
    void testAddUserToPlaylist_UserDoesNotExists(){
        //GIVEN
        String playlistId = "1234";
        String username = "username";
        //WHEN
        when(userService.findByUsername(username)).thenReturn(null);
        //THEN
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            playlistService.addUserToPlaylist(playlistId, username);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User not found", exception.getReason());
    }
}


