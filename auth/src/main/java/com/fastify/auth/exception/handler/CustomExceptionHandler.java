package com.fastify.auth.exception.handler;

import com.fastify.auth.model.dto.exception.FieldValidationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldValidationDto> validations = e.getBindingResult().getFieldErrors().stream()
                .map(FieldValidationDto::new)
                .toList();
        return ResponseEntity.badRequest().body(validations);
    }

    private String currentTimestamp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }
}
