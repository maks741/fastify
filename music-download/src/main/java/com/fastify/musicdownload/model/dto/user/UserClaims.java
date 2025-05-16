package com.fastify.musicdownload.model.dto.user;

import com.fastify.musicdownload.model.enumeration.Role;

public record UserClaims(
        Long userId,
        String userEmail,
        Role userRole
) {
}
