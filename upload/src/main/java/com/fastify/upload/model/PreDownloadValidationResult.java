package com.fastify.upload.model;

public record PreDownloadValidationResult(
        String videoId,
        String uploader,
        String title,
        Double filesize,
        Double filesizeMb
) {
}
