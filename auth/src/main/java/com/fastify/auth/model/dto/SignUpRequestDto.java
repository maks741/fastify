package com.fastify.auth.model.dto;

public record SignUpRequestDto(
        String username,
        String email
) {
}
