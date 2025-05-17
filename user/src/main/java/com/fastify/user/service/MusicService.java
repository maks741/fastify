package com.fastify.user.service;

import com.fastify.user.model.dto.MusicDto;
import com.fastify.user.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public List<MusicDto> findAllByUserId(Long userId) {
        return musicRepository.findAllByUserId(userId).stream()
                .map(music -> new MusicDto(
                        music.getVideoId(),
                        music.getUrl()
                ))
                .toList();
    }
}
