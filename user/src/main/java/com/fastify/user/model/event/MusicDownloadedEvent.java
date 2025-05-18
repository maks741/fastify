package com.fastify.user.model.event;

public record MusicDownloadedEvent(
        Long userId,
        Long musicId,
        String videoId,
        String musicUrl,
        String uploader,
        String title
) {
}
