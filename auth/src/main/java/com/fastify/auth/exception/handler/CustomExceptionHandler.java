package com.fastify.auth.exception.handler;

import com.fastify.auth.exception.DuplicateEmailException;
import com.fastify.auth.exception.PublishingException;
import com.fastify.auth.model.dto.exception.ExceptionDto;
import com.fastify.auth.model.dto.exception.FieldValidationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldValidationDto> validations = e.getBindingResult().getFieldErrors().stream()
                .map(FieldValidationDto::new)
                .toList();
        ExceptionDto exceptionDto = new ExceptionDto(validations);
        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDto> handleAuthenticationException(AuthenticationException e) {
        String message = "Authorize first";
        ExceptionDto exceptionDto = new ExceptionDto(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionDto);
    }

    @ExceptionHandler(PublishingException.class)
    public ResponseEntity<ExceptionDto> handlePublishingException(PublishingException e) {
        String message = "Something went wrong";
        ExceptionDto exceptionDto = new ExceptionDto(message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDto);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ExceptionDto> handleDuplicateEmailException(DuplicateEmailException e) {
        String message = "User with that email already exists";
        ExceptionDto exceptionDto = new ExceptionDto(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDto);
    }
}
