package com.fastify.upload.model.dto.user;

import com.fastify.upload.model.enumeration.Role;

public record UserClaims(
        Long userId,
        String userEmail,
        Role userRole
) {
}
