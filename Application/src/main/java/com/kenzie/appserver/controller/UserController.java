package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private UserService userService;

    UserController(UserService userService){this.userService = userService;}

    //Working
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest userCreateRequest){

        User user = new User(
                UUID.randomUUID().toString(),
                userCreateRequest.getUsername(),
                userCreateRequest.getPassword(),
                userCreateRequest.getEmail(),
                //this might be the issue
                new ArrayList<>()
        );

        userService.addNewUser(user);
        UserResponse userResponse = createUserResponse(user);
        return ResponseEntity.created(URI.create("/user/" + userResponse.getUserId())).body(userResponse);
    }

    //working
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable ("username") String username){
        User user = userService.findByUsername(username);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        UserResponse userResponse = createUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return ResponseEntity.ok(user.getUserId());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    private UserResponse createUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setUsername(user.getUserName());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setPlaylists(user.getPlaylists());

        return userResponse;
    }
}
