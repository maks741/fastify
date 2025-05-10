package com.fastify.auth.model.dto;

public record SignUpResponseDto(
        Long id,
        String token,
        String username,
        String email
) {
}
