package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.User;
import com.kenzie.capstone.service.client.LambdaServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LambdaServiceClient lambdaServiceClient;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, lambdaServiceClient);
    }

    @Test
    void testFindByUsername_UserExists() {
        // GIVEN
        String username = "user";
        User expectedUser = new User(
                "userId",
                "username",
                "password",
                "email",
                new ArrayList<>()
        );
        UserRecord userRecord = new UserRecord();

        userRecord.setUserId(expectedUser.getUserId());
        userRecord.setUserName(expectedUser.getUserName());
        userRecord.setPassword(expectedUser.getPassword());
        userRecord.setEmail(expectedUser.getEmail());
        userRecord.setPlaylists(expectedUser.getPlaylists());

        Mockito.when(userRepository.findById(username)).thenReturn(Optional.of(userRecord));
        // WHEN
        User result = userService.findByUsername(username);

        //THEN
        assertEquals(expectedUser.getUserId(), result.getUserId());
        assertEquals(expectedUser.getUserName(), result.getUserName());
        assertEquals(expectedUser.getPassword(), result.getPassword());
        assertEquals(expectedUser.getEmail(), result.getEmail());
        assertEquals(expectedUser.getPlaylists(), result.getPlaylists());
    }

    @Test
    void testFindByUsername_UserDoesNotExists() {
        // GIVEN
        String username = "user";

        Mockito.when(userRepository.findById(username)).thenReturn(Optional.empty());
        // WHEN//THEN
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            userService.findByUsername(username);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("UserId does not exist", e.getReason());
    }

    @Test
    void testAddPlaylistToUser() {
        // GIVEN
        String username = "user";
        List<String> initialPlaylists = new ArrayList<>();
        initialPlaylists.add("1");
        User expectedUser = new User(
                "userId",
                "username",
                "password",
                "email",
                initialPlaylists
        );
        UserRecord userRecord = new UserRecord();

        userRecord.setUserId(expectedUser.getUserId());
        userRecord.setUserName(expectedUser.getUserName());
        userRecord.setPassword(expectedUser.getPassword());
        userRecord.setEmail(expectedUser.getEmail());
        userRecord.setPlaylists(expectedUser.getPlaylists());

        Mockito.when(userRepository.findById(username)).thenReturn(Optional.of(userRecord));
        // WHEN
        userService.addPlaylistToUser(username, "2");

        //THEN
        assertEquals(2, userRecord.getPlaylists().size());
        Mockito.verify(userRepository).save(userRecord);
    }

    @Test
    void testAddPlaylistToUserUserNotFound() {
        // GIVEN
        String username = "user";
        Mockito.when(userRepository.findById(username)).thenReturn(Optional.empty());
        // WHEN/THEN
        userService.addPlaylistToUser(username, "2");
    }

    @Test
    void testAddNewUser() {
        // Arrange
        User newUser = new User("new-user", "New User", "new-password", "newuser@example.com", new ArrayList<>());

        // Act
        userService.addNewUser(newUser);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }
}