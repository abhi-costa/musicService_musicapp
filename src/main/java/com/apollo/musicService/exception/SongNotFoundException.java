package com.apollo.musicService.exception;

/**
 * Custom exception thrown when a song is not found.
 */
public class SongNotFoundException extends RuntimeException {
	public SongNotFoundException(String id) {
		super("Song not found with id: " + id);

	}
}