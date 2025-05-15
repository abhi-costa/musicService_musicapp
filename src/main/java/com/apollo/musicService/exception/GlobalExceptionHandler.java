/*
 * package com.apollo.musicService.exception;
 * 
 * import org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.web.bind.MethodArgumentNotValidException; import
 * org.springframework.web.bind.annotation.*; import
 * org.springframework.web.multipart.MaxUploadSizeExceededException;
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * @RestControllerAdvice public class GlobalExceptionHandler {
 * 
 * @ExceptionHandler(SongNotFoundException.class) public
 * ResponseEntity<Map<String, String>> handleSongNotFound(SongNotFoundException
 * ex) { Map<String, String> error = new HashMap<>(); error.put("error",
 * ex.getMessage()); return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); }
 * 
 * @ExceptionHandler(MethodArgumentNotValidException.class) public
 * ResponseEntity<Map<String, String>>
 * handleValidation(MethodArgumentNotValidException ex) { Map<String, String>
 * errors = new HashMap<>(); ex.getBindingResult().getFieldErrors()
 * .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
 * return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); }
 * 
 * @ExceptionHandler(IllegalArgumentException.class) public
 * ResponseEntity<Map<String, String>>
 * handleIllegalArgument(IllegalArgumentException ex) { Map<String, String>
 * error = new HashMap<>(); error.put("error", ex.getMessage()); return new
 * ResponseEntity<>(error, HttpStatus.BAD_REQUEST); }
 * 
 * @ExceptionHandler(MaxUploadSizeExceededException.class) public
 * ResponseEntity<Map<String, String>>
 * handleFileSize(MaxUploadSizeExceededException ex) { Map<String, String> error
 * = new HashMap<>(); error.put("error", "File size exceeds maximum limit!");
 * return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE); }
 * 
 * @ExceptionHandler(Exception.class) public ResponseEntity<Map<String, String>>
 * handleGeneral(Exception ex) { Map<String, String> error = new HashMap<>();
 * error.put("error", "Something went wrong: " + ex.getMessage()); return new
 * ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); } }
 */