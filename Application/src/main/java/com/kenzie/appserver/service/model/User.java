package com.kenzie.appserver.service.model;

import java.util.List;

public class User {

    private final String userId;
    private final String userName;
    private final String password;

    private final String email;
    private final List<String> playlists;

    public User(String userId, String userName, String password, String email, List<String> playlists){
        this.userId = userId;
        this.userName=userName;
        this.password = password;
        this.email = email;
        this.playlists = playlists;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {return email;}

    public List<String> getPlaylists() {
        return playlists;
    }
}
