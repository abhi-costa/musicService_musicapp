package com.apollo.musicService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.apollo.musicService.model.Song;

public interface SongRepository extends MongoRepository<Song, String> {
}