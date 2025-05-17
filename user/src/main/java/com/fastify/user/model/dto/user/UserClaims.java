package com.fastify.user.model.dto.user;

import com.fastify.user.model.enumeration.Role;

public record UserClaims(
        Long userId,
        String userEmail,
        Role userRole
) {
}
