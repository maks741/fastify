package com.fastify.user.service;

import com.fastify.user.model.dto.user.UserClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev", "test"})
@Service
public class LocalFileStorageService implements FileStorage {
    private final String localStorageHost;
    private final String thumbnailSuffix;
    private final String thumbnailFormat;

    public LocalFileStorageService(
            @Value("${local.storage.host}") String localStorageHost,
            @Value("${local.objects.suffix.thumbnail}") String thumbnailSuffix,
            @Value("${local.objects.format.thumbnail}") String thumbnailFormat
    ) {
        this.localStorageHost = localStorageHost;
        this.thumbnailSuffix = thumbnailSuffix;
        this.thumbnailFormat = thumbnailFormat;
    }

    @Override
    public String generateThumbnailUrl(UserClaims userClaims, String videoId) {
        String storageSubFolderPath = userClaims.userId() + "/" + videoId + "/";
        String thumbnailPathStr = storageSubFolderPath + thumbnailSuffix + "." + thumbnailFormat;

        return localStorageHost + thumbnailPathStr;
    }
}
