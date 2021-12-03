package com.mycompany.appointmentservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvisor {

    @ExceptionHandler(DataAlreadyExistsException.class)
    public ResponseEntity<String> handleDataAlreadyExistsException(DataAlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DataInvalidException.class)
    public ResponseEntity<String> handleInvalidDataException(DataInvalidException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
