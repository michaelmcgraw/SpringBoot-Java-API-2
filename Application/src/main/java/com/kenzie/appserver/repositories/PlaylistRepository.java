package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.PlaylistRecord;
import com.kenzie.appserver.service.model.Playlist;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableScan
public interface PlaylistRepository extends CrudRepository<PlaylistRecord,String> {
    default List<Playlist> findPlaylistsByUserId(String id) {
        List<PlaylistRecord> playlistRecords = findByUserIdList(id);
        System.out.println("Repository: Retrieved records for user ID: " + id); // Logging
        System.out.println(playlistRecords); // Logging the raw data

        return playlistRecords.stream()
                .map(playlistRecord -> new Playlist(
                        playlistRecord.getPlaylistId(),
                        playlistRecord.getPlaylistName(),
                        playlistRecord.getSongs(),
                        playlistRecord.getUserIdList(),
                        playlistRecord.getSpotifyPlaylistURI()
                ))
                .collect(Collectors.toList());
    }

    List<PlaylistRecord> findByUserIdList(String id);
}
