package com.fastify.download.model.event;

public record MusicDownloadedEvent(
        Long userId,
        Long musicId,
        String videoId,
        String musicUrl
) {
}
