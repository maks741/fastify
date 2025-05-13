package com.fastify.musicdownload.model;

public record PreDownloadValidationResult(
        String videoId,
        String uploader,
        String title,
        Double filesize,
        Double filesizeMb
) {
}
