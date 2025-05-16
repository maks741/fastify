package com.fastify.download.model;

public record DownloadResult(
        String videoId,
        String uploader,
        String title,
        Double filesize,
        Double filesizeMb,
        String audioPath,
        String thumbnailPath
) {
}
