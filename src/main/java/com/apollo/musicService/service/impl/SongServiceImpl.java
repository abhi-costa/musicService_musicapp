package com.apollo.musicService.service.impl;

import com.apollo.musicService.exception.SongNotFoundException;
import com.apollo.musicService.model.Song;
import com.apollo.musicService.repository.SongRepository;
import com.apollo.musicService.service.MinioService;
import com.apollo.musicService.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Implementation of SongService for handling song metadata and MP3 file
 * storage.
 */
@Service
public class SongServiceImpl implements SongService {

	private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);

	private final SongRepository songRepository;
	private final MinioService minioService;

	public SongServiceImpl(SongRepository songRepository, MinioService minioService) {
		this.songRepository = songRepository;
		this.minioService = minioService;
	}

	/**
	 * Saves a song and uploads its MP3 file to MinIO.
	 *
	 * @param song Song metadata.
	 * @param file MultipartFile representing the MP3 file.
	 * @return Saved song with file URL.
	 */
	@Override
	public Song saveSong(Song song, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is empty");
		}

		logger.info("Uploading song file: {}", file.getOriginalFilename());

		String fileUrl = minioService.uploadFile(file);
		song.setFileUrl(fileUrl);

		Song savedSong = songRepository.save(song);
		logger.info("Song saved with ID: {}", savedSong.getId());

		return savedSong;
	}

	/**
	 * Retrieves all songs from the repository.
	 *
	 * @return List of songs.
	 */
	@Override
	public List<Song> getAllSongs() {
		return songRepository.findAll();
	}

	/**
	 * Retrieves a song by its ID.
	 *
	 * @param id Song ID.
	 * @return Song object if found.
	 * @throws SongNotFoundException if the song is not found.
	 */
	@Override
	public Song getSongById(String id) {
		return songRepository.findById(id).orElseThrow(() -> new SongNotFoundException(id));
	}

	/**
	 * Deletes a song by ID and its associated file from MinIO.
	 *
	 * @param id Song ID.
	 * @throws SongNotFoundException if the song is not found.
	 */
	@Override
	public void deleteSong(String id) {
		Song song = getSongById(id);

		logger.info("Deleting song with ID: {}", id);

		minioService.deleteFile(song.getFileUrl()); // Optional: Only if implemented in MinioService
		songRepository.deleteById(id);

		logger.info("Song deleted with ID: {}", id);
	}
}
