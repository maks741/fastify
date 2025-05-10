package com.fastify.auth.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequestDto(
        @NotNull(message = "username is required")
        @Size.List({
                @Size(min = 1, message = "username cannot be empty"),
                @Size(max = 200, message = "username cannot be longer than {max} characters")
        })
        String username,
        @NotNull(message = "email is required")
        @Size.List({
                @Size(min = 1, message = "email cannot be empty"),
                @Size(max = 200, message = "email cannot be longer than {max} characters")
        })
        @Pattern(
                regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                message = "Please enter valid email"
        )
        String email
) {
}