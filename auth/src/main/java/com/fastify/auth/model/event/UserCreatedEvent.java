package com.fastify.auth.model.event;

import com.fastify.auth.model.enumeration.Role;

public record UserCreatedEvent(
        Long userId,
        String userEmail,
        Role userRole
) {
}