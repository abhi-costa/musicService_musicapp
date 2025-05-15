package com.apollo.musicService.service;

import com.apollo.musicService.model.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {

    Song saveSong(Song song, MultipartFile file);

    List<Song> getAllSongs();

    Song getSongById(String id);

    void deleteSong(String id);
}
