package com.fastify.user.model.dto;

public record MusicDto(
        String videoId,
        String url,
        String thumbnailUrl,
        String uploader,
        String title
) {
}
