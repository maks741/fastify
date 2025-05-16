package com.fastify.upload.model.dto;

import lombok.Builder;

@Builder
public record DownloadResultDto(
        String videoId,
        String uploader,
        String title
) {
}
