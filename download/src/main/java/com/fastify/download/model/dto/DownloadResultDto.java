package com.fastify.download.model.dto;

import lombok.Builder;

@Builder
public record DownloadResultDto(
        String videoId,
        String url,
        String thumbnailUrl,
        String uploader,
        String title
) {
}
