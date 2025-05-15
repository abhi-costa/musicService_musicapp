package com.apollo.musicService.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apollo.musicService.model.Song;
import com.apollo.musicService.service.MinioService;
import com.apollo.musicService.service.SongService;

@RestController
@RequestMapping("/songs")
public class SongController {

	private final SongService songService;
	private final MinioService minioService;
	private static final Logger logger = LoggerFactory.getLogger(SongController.class);

	// Constructor-based injection of the SongService interface
	public SongController(SongService songService, MinioService minioService) {
		this.songService = songService;
		this.minioService = minioService;
	}

	/**
	 * Endpoint to upload a new song with metadata and MP3 file.
	 *
	 * @param file   the audio file to upload
	 * @param name   name of the song
	 * @param artist name of the artist
	 * @param year   year of release
	 * @return the saved song or an error response
	 */
	@PostMapping("/upload")
	public ResponseEntity<Song> uploadSong(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
			@RequestParam("artist") String artist, @RequestParam("year") int year) {
		if (file.isEmpty()) {
			logger.error("File is empty.");
			return ResponseEntity.badRequest().build();
		}

		if (name.isEmpty() || artist.isEmpty()) {
			logger.error("Name or artist is missing.");
			return ResponseEntity.badRequest().build();
		}

		try {
			Song song = new Song();
			song.setName(name);
			song.setArtist(artist);
			song.setYear(year);

			Song saved = songService.saveSong(song, file);
			return ResponseEntity.ok(saved);
		} catch (Exception e) {
			logger.error("Failed to upload song: {}", e.getMessage());
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * Endpoint to retrieve all songs from the database.
	 *
	 * @return list of all songs
	 */
	@GetMapping
	public ResponseEntity<List<Song>> getAllSongs() {
		try {
			List<Song> songs = songService.getAllSongs();
			return ResponseEntity.ok(songs);
		} catch (Exception e) {
			logger.error("Failed to retrieve songs: {}", e.getMessage());
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 * Endpoint to retrieve a song by ID.
	 *
	 * @param id song ID
	 * @return the song if found, or 404 Not Found with a custom message
	 */

	@GetMapping("/{id}")
	public ResponseEntity<Object> getSong(@PathVariable String id) {
		try {
			if (!ObjectId.isValid(id)) {
				return ResponseEntity.status(400).body("Invalid song ID format: " + id);
			}

			Song song = songService.getSongById(id);
			if (song != null) {
				return ResponseEntity.ok(song);
			} else {
				return ResponseEntity.status(404).body("Song not found with ID: " + id);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve song with ID '{}': {}", id, e.getMessage());
			return ResponseEntity.status(500).body("Failed to retrieve song.");
		}
	}

	/**
	 * Endpoint to delete a song by ID.
	 *
	 * @param id song ID to delete
	 * @return a custom message indicating the result (deleted or not found)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteSong(@PathVariable String id) {
		try {
			Song song = songService.getSongById(id);

			if (song != null) {
				songService.deleteSong(id);
				logger.info("Song with ID '{}' has been deleted.", id);
				return ResponseEntity.ok("Song successfully deleted with this ID: " + id);
			} else {
				return ResponseEntity.status(404).body("Song not found with ID: " + id);
			}
		} catch (Exception e) {
			logger.error("Failed to delete song with ID '{}': {}", id, e.getMessage());
			return ResponseEntity.status(500).body("Failed to delete song.");
		}
	}

	@GetMapping("/songs/download/{fileName}")
	public ResponseEntity<Resource> downloadSong(@PathVariable String fileName) {
		Resource file = minioService.getFile(fileName);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(file);
	}

}