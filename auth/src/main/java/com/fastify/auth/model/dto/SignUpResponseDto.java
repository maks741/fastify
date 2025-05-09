package com.fastify.auth.model.dto;

public record SignUpResponseDto(
        Long id,
        String username,
        String email
) {
}
