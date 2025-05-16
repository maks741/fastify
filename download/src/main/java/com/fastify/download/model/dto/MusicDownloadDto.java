package com.fastify.download.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MusicDownloadDto(
        @NotNull(message = "url cannot be null")
        @Size.List({
                @Size(min = 1, message = "url cannot be empty"),
                @Size(max = 1000, message = "url is too long")
        })
        @Pattern(regexp = "^http[s,]://.*", message = "invalid url")
        String url
) {
}
