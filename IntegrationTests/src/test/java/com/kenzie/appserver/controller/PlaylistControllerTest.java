package com.kenzie.appserver.controller;

import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.PlaylistCreateRequest;
import com.kenzie.appserver.controller.model.PlaylistResponse;
import com.kenzie.appserver.service.CachingPlaylistService;
import com.kenzie.appserver.service.PlaylistService;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.Playlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kenzie.appserver.service.model.User;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class PlaylistControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CachingPlaylistService playlistService;
    @Mock
    private UserService userService;

    @InjectMocks
    private PlaylistController playlistController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build();
    }

    @Test
    void addPlaylist_ValidRequest_ShouldReturnCreated() throws Exception {
        // Given
        PlaylistCreateRequest request = new PlaylistCreateRequest();
        request.setPlaylistName("Test Playlist");

        Playlist createdPlaylist = new Playlist(
                UUID.randomUUID().toString(),
                "Test Playlist",
                new ArrayList<>(),
                List.of("temp"),
                "temp"
        );

        when(playlistService.addNewPlaylist(any(Playlist.class))).thenReturn(createdPlaylist);

        // When/then
        mockMvc.perform(post("/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("playlistName").value("Test Playlist"));
    }

    @Test
    void getPlaylistById_ExistingPlaylist_ShouldReturnOk() throws Exception {
        // Given
        String playlistId = UUID.randomUUID().toString();
        Playlist existingPlaylist = new Playlist(
                playlistId,
                "Test Playlist",
                new ArrayList<>(),
                List.of("temp"),
                "temp"
        );

        // When/then
        when(playlistService.findByPlaylistId(playlistId)).thenReturn(existingPlaylist);
        mockMvc.perform(get("/playlists/" + playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("playlistName").value("Test Playlist"));
    }

    @Test
    void getAllPlaylistsByUserId_Existing_ShouldReturnOk() throws Exception {
        // Given
        String userId = "user123";

        List<Playlist> samplePlaylists = new ArrayList<>();
        samplePlaylists.add(new Playlist("playlist1", "Playlist 1", new ArrayList<>(), List.of(userId), "temp"));
        samplePlaylists.add(new Playlist("playlist2", "Playlist 2", new ArrayList<>(), List.of(userId), "temp"));

        // When/then
        when(playlistService.getAllPlaylistsByUserId(userId)).thenReturn(samplePlaylists);
        mockMvc.perform(MockMvcRequestBuilders.get("/playlists")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Assuming there are 2 playlists for the user
                .andExpect(jsonPath("[0].playlistName", is("Playlist 1"))) // Check the first playlist's name
                .andExpect(jsonPath("[1].playlistName", is("Playlist 2"))); // Check the second playlist's name
    }

    @Test
    void addSongToPlaylist_ValidRequest_ShouldReturnOk() throws Exception {
        // Given
        String playlistId = "playlist123";
        String songRequest = "song123";

        // When/then
        mockMvc.perform(MockMvcRequestBuilders.put("/playlists/{playlistId}/add-song", playlistId)
                        .param("songRequest", songRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addUserToPlaylist_ValidRequest_ShouldReturnOk() throws Exception {
        // Given
        String playlistId = "playlist123";
        String username = "user123";

        User existingUser = new User("UserId", username, "password", "email", new ArrayList<>());
        Playlist existingPlaylist = new Playlist(playlistId, "Playlist Name", new ArrayList<>(), List.of("temp"), "temp");

        // When
        when(userService.findByUsername(username)).thenReturn(existingUser);
        when(playlistService.findByPlaylistId(playlistId)).thenReturn(existingPlaylist);
        doNothing().when(playlistService).addUserToPlaylist(playlistId, username);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/playlists/{playlistId}/users/{username}/add", playlistId, username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addUserToPlaylist_Failure_ShouldReturnBadRequest() throws Exception {
        // Given
        String playlistId = "playlist123";
        String username = "user123";

        // Simulate a failure scenario
        when(userService.findByUsername(username)).thenReturn(new User("UserId", username, "password", "email", new ArrayList<>()));
        when(playlistService.findByPlaylistId(playlistId)).thenReturn(new Playlist(playlistId, "Playlist Name", new ArrayList<>(), List.of("temp"), "temp"));

        doThrow(new RuntimeException("Simulated failure")).when(playlistService).addUserToPlaylist(playlistId, username);

        // When/then
        mockMvc.perform(MockMvcRequestBuilders.post("/playlists/{playlistId}/users/{username}/add", playlistId, username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

