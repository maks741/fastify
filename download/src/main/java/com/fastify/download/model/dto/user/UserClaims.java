package com.fastify.download.model.dto.user;

import com.fastify.download.model.enumeration.Role;

public record UserClaims(
        Long userId,
        String userEmail,
        Role userRole
) {
}
