package com.fastify.musicdownload.model;

public record DownloadResult(
        String videoId,
        String uploader,
        String title,
        String audioPath,
        String thumbnailPath
) {
}
