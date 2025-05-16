package com.fastify.upload.model.event;

import com.fastify.upload.model.enumeration.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserCreatedEvent(
        @NotNull
        Long userId,
        @NotNull
        @NotEmpty
        String userEmail,
        @NotNull
        Role userRole
) {
}