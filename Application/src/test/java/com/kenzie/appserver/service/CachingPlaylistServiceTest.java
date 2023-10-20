package com.kenzie.appserver.service;

import com.google.gson.Gson;
import com.kenzie.appserver.cache.CacheClient;
import com.kenzie.appserver.service.model.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachingPlaylistServiceTest {

    @InjectMocks
    private CachingPlaylistService cachingPlaylistService;



    @Mock
    private CacheClient cacheClient;



    @Mock
    private PlaylistService playlistService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }






    @Test
    void testFindByPlaylistId_withoutCachedValue() {
        Playlist mockPlaylist = new Playlist("testId", "testName", Arrays.asList("song1", "song2"), Arrays.asList("user1"), "spotifyUri");

        when(cacheClient.getValue("testId")).thenReturn(null);
        when(playlistService.findByPlaylistId("testId")).thenReturn(mockPlaylist);

        Playlist result = cachingPlaylistService.findByPlaylistId("testId");

        assertEquals(mockPlaylist, result);
        verify(cacheClient).getValue("testId");
        verify(playlistService).findByPlaylistId("testId");
    }

    @Test
    void testGetAllPlaylists() {
        List<Playlist> mockPlaylists = Arrays.asList(
                new Playlist("testId1", "testName1", Arrays.asList("song1"), Arrays.asList("user1"), "spotifyUri1"),
                new Playlist("testId2", "testName2", Arrays.asList("song2"), Arrays.asList("user2"), "spotifyUri2")
        );

        when(playlistService.getAllPlaylists()).thenReturn(mockPlaylists);

        List<Playlist> result = cachingPlaylistService.getAllPlaylists();

        assertEquals(mockPlaylists, result);
        verify(playlistService).getAllPlaylists();
    }


    @Test
    void testGetAllPlaylistsByUserId_withoutCachedValue() {
        String userId = "user123";
        List<Playlist> mockPlaylists = Arrays.asList(
                new Playlist("testId1", "testName1", Arrays.asList("song1"), Arrays.asList(userId), "spotifyUri1"),
                new Playlist("testId2", "testName2", Arrays.asList("song2"), Arrays.asList(userId), "spotifyUri2")
        );

        when(cacheClient.getValue(userId)).thenReturn(null);
        when(playlistService.getAllPlaylistsByUserId(userId)).thenReturn(mockPlaylists);

        List<Playlist> result = cachingPlaylistService.getAllPlaylistsByUserId(userId);

        assertEquals(mockPlaylists, result);
        verify(cacheClient).getValue(userId);
        verify(playlistService).getAllPlaylistsByUserId(userId);
        verify(cacheClient).setValue(eq(userId), eq(60), anyString());
    }

    @Test
    void testAddSongToPlaylist() {
        String playlistId = "testId1";
        String song = "song3";

        Playlist mockPlaylist = new Playlist(playlistId, "testName1", Arrays.asList("song1", "song2"), Arrays.asList("user1"), "spotifyUri1");

        when(playlistService.addSongToPlaylist(playlistId, song)).thenReturn(mockPlaylist);

        Playlist result = cachingPlaylistService.addSongToPlaylist(playlistId, song);

        assertEquals(mockPlaylist, result);
        verify(cacheClient).deleteValue(playlistId);
    }
    @Test
    void testAddUserToPlaylist() throws Exception {
        String playlistId = "testId1";
        String username = "user1";

        cachingPlaylistService.addUserToPlaylist(playlistId, username);

        verify(cacheClient).deleteValue(playlistId);
        verify(playlistService).addUserToPlaylist(playlistId, username);
    }


    @Test
    void testAddNewPlaylist() {
        Playlist mockPlaylist = new Playlist("testId1", "testName1", Arrays.asList("song1"), Arrays.asList("user1"), "spotifyUri1");

        when(playlistService.addNewPlaylist(mockPlaylist)).thenReturn(mockPlaylist);

        Playlist result = cachingPlaylistService.addNewPlaylist(mockPlaylist);

        assertEquals(mockPlaylist, result);
        verify(playlistService).addNewPlaylist(mockPlaylist);
    }

    @Test
    void testUpdatePlaylist() {
        Playlist mockPlaylist = new Playlist("testId1", "testName1", Arrays.asList("song1"), Arrays.asList("user1"), "spotifyUri1");

        cachingPlaylistService.updatePlaylist(mockPlaylist);

        verify(playlistService).updatePlaylist(mockPlaylist);
    }
    /*@Test
    void testGetAllPlaylistsByUserId_withCachedValue() {
        String userId = "testUser1";
        List<Playlist> mockPlaylists = Arrays.asList(
                new Playlist("testId1", "testName1", Arrays.asList("song1"), Arrays.asList("user1"), "spotifyUri1")
        );
        String json = new Gson().toJson(mockPlaylists);

        when(cacheClient.getValue(userId)).thenReturn(json);

        List<Playlist> result = cachingPlaylistService.getAllPlaylistsByUserId(userId);

        assertEquals(mockPlaylists, result);
        verify(cacheClient).getValue(userId);
        verify(playlistService, never()).getAllPlaylistsByUserId(userId);
    }

     */

    @Test
    void testFindByPlaylistId_withCachedValue() {
        String playlistId = "testId1";
        Playlist mockPlaylist = new Playlist(playlistId, "testName1", Arrays.asList("song1"), Arrays.asList("user1"), "spotifyUri1");
        String json = new Gson().toJson(mockPlaylist);

        when(cacheClient.getValue(playlistId)).thenReturn(json);

        Playlist result = cachingPlaylistService.findByPlaylistId(playlistId);

        assertEquals(mockPlaylist, result);
        verify(cacheClient).getValue(playlistId);
        verify(playlistService, never()).findByPlaylistId(anyString());
    }



}
