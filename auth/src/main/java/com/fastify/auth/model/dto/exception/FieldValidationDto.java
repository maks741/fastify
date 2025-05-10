package com.fastify.auth.model.dto.exception;

import org.springframework.validation.FieldError;

public record FieldValidationDto(
        String field,
        String message
) {
    public FieldValidationDto(FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
