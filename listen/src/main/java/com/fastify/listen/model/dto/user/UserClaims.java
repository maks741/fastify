package com.fastify.listen.model.dto.user;

import com.fastify.listen.model.enumeration.Role;

public record UserClaims(
        Long userId,
        String userEmail,
        Role userRole
) {
}
