package com.fastify.download.service;

import com.fastify.download.model.DownloadResult;

public interface FileStorage {
    void store(Long userId, DownloadResult downloadResult);
    String generateThumbnailUrl(Long userId, String videoId);
}
