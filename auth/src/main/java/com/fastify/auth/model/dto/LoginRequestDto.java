package com.fastify.auth.model.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
