package com.fastify.download.model.event;

import lombok.Builder;

@Builder
public record MusicDownloadedEvent(
        Long userId,
        Long musicId,
        String videoId,
        String musicUrl,
        String uploader,
        String title
) {
}
