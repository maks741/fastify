package com.fastify.user.service;

import com.fastify.user.model.dto.user.UserClaims;

public interface FileStorage {
    String generateThumbnailUrl(UserClaims userClaims, String videoId);
}
