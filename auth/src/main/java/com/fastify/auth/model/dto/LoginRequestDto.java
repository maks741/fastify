package com.fastify.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(
        @NotNull(message = "email is required")
        @NotBlank(message = "password is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                message = "Please enter valid email"
        )
        String email,
        @NotBlank(message = "password is required")
        String password
) {
}
