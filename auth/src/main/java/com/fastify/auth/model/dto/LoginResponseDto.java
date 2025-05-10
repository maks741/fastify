package com.fastify.auth.model.dto;

public record LoginResponseDto(
        Long id,
        String token,
        String username,
        String email
) {
}
