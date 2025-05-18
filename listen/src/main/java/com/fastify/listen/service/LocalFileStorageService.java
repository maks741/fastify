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

    public LocalFileStorageService(
            @Value("${local.storage.host}") String localStorageHost
    ) {
        this.localStorageHost = localStorageHost;
    }

    @Override
    public ListenResponse generateAudioUrl(UserClaims userClaims, String videoId) {
        String storageSubFolderPath = userClaims.userId() + "/" + videoId + "/";
        String thumbnailPathStr = storageSubFolderPath + "audio" + "." + "mp3";

        String audioResourceUrl = localStorageHost + thumbnailPathStr;
        return new ListenResponse(videoId, audioResourceUrl);
    }
}
