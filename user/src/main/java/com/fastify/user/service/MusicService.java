package com.fastify.user.service;

import com.fastify.user.model.dto.MusicDto;
import com.fastify.user.model.dto.user.UserClaims;
import com.fastify.user.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final FileStorage fileStorage;

    public List<MusicDto> findAllByUserId(UserClaims userClaims) {
        Long userId = userClaims.userId();
        return musicRepository.findAllByUserId(userId).stream()
                .map(music -> {
                    String thumbnailUrl = fileStorage.generateThumbnailUrl(userClaims, music.getVideoId());

                    return new MusicDto(
                            music.getVideoId(),
                            music.getUrl(),
                            thumbnailUrl,
                            music.getUploader(),
                            music.getTitle()
                    );
                })
                .toList();
    }
}
