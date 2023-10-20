package com.kenzie.appserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.controller.UserController;
import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_ValidRequest_ShouldReturnCreated() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("TestUser");
        request.setPassword("TestPassword");
        request.setEmail("test@example.com");

        // When/then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId").isNotEmpty())
                .andReturn();

        String userId = result.getResponse().getContentAsString();
        assertNotNull(userId);
    }

    @Test
    void getUserByUsername_ExistingUser_ShouldReturnOk() throws Exception {
        // Given
        String username = "TestUser";
        User existingUser = new User(
                "123456789", // Replace with a valid user ID
                "TestUser",
                "TestPassword",
                "test@example.com",
                new ArrayList<>()
        );

        when(userService.findByUsername(username)).thenReturn(existingUser);

        // When/then
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(existingUser.getUserName()));
    }

    @Test
    void getUserByUsername_NonExistingUser_ShouldReturnNotFound() throws Exception {
        // Given
        String username = "NonExistentUser";

        // User does not exist in the database
        when(userService.findByUsername(username)).thenReturn(null);

        // When/then
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginUser_SuccessfulLogin_ShouldReturnOk() throws Exception {
        // Given
        String username = "TestUser";
        String password = "TestPassword";
        String userId = "123456789"; // Replace with a valid user ID

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        User user = new User(userId, username, password, "test@example.com", null); // User exists in the database

        // When/then
        when(userService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"TestUser\", \"password\": \"TestPassword\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void loginUser_AuthenticationFailure_ShouldReturnUnauthorized() throws Exception {
        // Given
        String username = "TestUser";
        String password = "TestPassword";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // User does not exist in the database
        when(userService.findByUsername(username)).thenReturn(null);

        // When/then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"TestUser\", \"password\": \"TestPassword\"}"))
                .andExpect(status().isUnauthorized());
    }
}
