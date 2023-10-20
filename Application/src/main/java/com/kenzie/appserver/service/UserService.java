package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.Playlist;
import com.kenzie.appserver.service.model.User;
import com.kenzie.capstone.service.client.LambdaServiceClient;
import org.checkerframework.checker.guieffect.qual.UI;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;
    private LambdaServiceClient lambdaServiceClient;

    public UserService(UserRepository userRepository, LambdaServiceClient lambdaServiceClient){
        this.userRepository = userRepository;
        this.lambdaServiceClient = lambdaServiceClient;
    }


    public User findByUsername(String username){
        User userFromRepository = userRepository
                .findById(username)
                .map(userRecord -> new User(userRecord.getUserId(),userRecord.getUserName(),userRecord.getPassword(), userRecord.getEmail(), userRecord.getPlaylists()))
                .orElse(null);
        if (userFromRepository != null){
            return userFromRepository;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"UserId does not exist");
        }
    }

    //This needs to be redone
    public void addPlaylistToUser(String username, String playlistId){
        UserRecord existingUserRecord = userRepository.findById(username).orElse(null);

        if (existingUserRecord != null){
            List<String> playlists = existingUserRecord.getPlaylists();
            playlists.add(playlistId);
            userRepository.save(existingUserRecord);
        }
    }

    public void addNewUser(User user){
        UserRecord userRecord = new UserRecord();

        userRecord.setUserId(user.getUserId());
        userRecord.setUserName(user.getUserName());
        userRecord.setPassword(user.getPassword());
        userRecord.setEmail(user.getEmail());
        userRecord.setPlaylists(user.getPlaylists());

        userRepository.save(userRecord);
    }

}
