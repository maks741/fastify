package com.fastify.download.model.dto;

import lombok.Builder;

@Builder
public record DownloadResultDto(
        String videoId,
        String url,
        String uploader,
        String title
) {
}
