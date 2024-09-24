package com.sistema.login.Exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExistingException.class)
    public ResponseEntity<StandardError> emailExisting(EmailExistingException exception) {
        StandardError error = new StandardError();
        error.setTimestamp(Instant.now());
        error.setMessage(exception.getMessage());
        error.setPath("N/A");
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Email Existing");

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<StandardError> handleJsonParseException(JsonParseException ex) {
        StandardError error = new StandardError();
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("JSON Parse Error");
        error.setPath("N/A");
        error.setMessage("Invalid fields");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        StandardError error = new StandardError();
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Validation error");
        error.setPath("N/A");
        error.setMessage("Missing fields");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGeneralExceptions(
            Exception ex) {

        StandardError error = new StandardError();
        error.setTimestamp(Instant.now());
        error.setMessage(ex.getMessage()); // Set message to "Invalid fields"
        error.setPath("N/A"); // Path set to N/A since HttpServletRequest is not available
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("Internal Server Error");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
