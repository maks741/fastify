package com.fastify.download.service;

import com.fastify.download.model.DownloadResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class LocalFileStorageService implements FileStorage {
    @Override
    public void store(Long userId, DownloadResult downloadResult) {

    }

    @Override
    public String generateThumbnailUrl(Long userId, String videoId) {
        return "";
    }
}
