package com.fastify.listen.service;

import com.fastify.listen.model.dto.ListenResponse;
import com.fastify.listen.model.dto.user.UserClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class LocalFileStorageService implements FileStorage {

    private final String localStorageHost;
    private final String audioSuffix;
    private final String audioFormat;

    public LocalFileStorageService(
            @Value("${local.storage.host}") String localStorageHost,
            @Value("${local.objects.suffix.audio}") String audioSuffix,
            @Value("${local.objects.format.audio}") String audioFormat
    ) {
        this.localStorageHost = localStorageHost;
        this.audioSuffix = audioSuffix;
        this.audioFormat = audioFormat;
    }

    @Override
    public ListenResponse generateAudioUrl(UserClaims userClaims, String videoId) {
        String storageSubFolderPath = userClaims.userId() + "/" + videoId + "/";
        String thumbnailPathStr = storageSubFolderPath + audioSuffix + "." + audioFormat;

        String audioResourceUrl = localStorageHost + thumbnailPathStr;
        return new ListenResponse(videoId, audioResourceUrl);
    }
}
