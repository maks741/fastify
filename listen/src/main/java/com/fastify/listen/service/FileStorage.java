package com.fastify.listen.service;

import com.fastify.listen.model.dto.ListenResponse;
import com.fastify.listen.model.dto.user.UserClaims;

public interface FileStorage {
    ListenResponse generateAudioUrl(UserClaims userClaims, String videoId);
}
