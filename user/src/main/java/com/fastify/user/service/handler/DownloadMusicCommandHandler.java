package com.fastify.user.service.handler;

import com.fastify.user.model.entity.Music;
import com.fastify.user.model.entity.User;
import com.fastify.user.model.event.MusicDownloadedEvent;
import com.fastify.user.repository.MusicRepository;
import com.fastify.user.service.UserService;
import com.fastify.user.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadMusicCommandHandler implements CommandHandler {

    private final JsonConverter jsonConverter;
    private final UserService userService;
    private final MusicRepository musicRepository;

    @Override
    public void handle(String payload) {
        MusicDownloadedEvent musicDownloadedEvent = jsonConverter.convert(payload, MusicDownloadedEvent.class);
        User user = userService.findById(musicDownloadedEvent.userId());
        Music music = new Music(
                musicDownloadedEvent.musicId(),
                musicDownloadedEvent.videoId(),
                musicDownloadedEvent.musicUrl(),
                user
        );
        musicRepository.save(music);
    }
}
