package com.fastify.user.exception.handler;

import com.fastify.user.exception.UserNotFoundException;
import com.fastify.user.model.dto.exception.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleUserNotFoundException(UserNotFoundException e) {
        String message = e.getMessage();
        ExceptionDto exceptionDto = new ExceptionDto(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }
}
