package com.fastify.user.service.handler;

import com.fastify.user.exception.BadPayloadException;
import com.fastify.user.exception.UserNotFoundException;
import com.fastify.user.model.entity.Music;
import com.fastify.user.model.entity.User;
import com.fastify.user.model.event.MusicDownloadedEvent;
import com.fastify.user.repository.MusicRepository;
import com.fastify.user.service.UserService;
import com.fastify.user.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadMusicCommandHandlerTest {

    @Mock
    MusicRepository musicRepository;

    @Mock
    UserService userService;

    @Mock
    JsonConverter jsonConverter;

    @InjectMocks
    DownloadMusicCommandHandler downloadMusicCommandHandler;

    @Test
    void handle_WhenValidPayloadProvided_ShouldSaveUser() {
        String payload = "payload";
        Long userId = 1L;
        MusicDownloadedEvent musicDownloadedEvent = new MusicDownloadedEvent(userId, 2L, "videoId", "musicUrl", "uploader", "title");
        User user = new User();
        Music music = new Music(
                musicDownloadedEvent.musicId(),
                musicDownloadedEvent.videoId(),
                musicDownloadedEvent.musicUrl(),
                musicDownloadedEvent.uploader(),
                musicDownloadedEvent.title(),
                user
        );
        ArgumentCaptor<Music> argumentCaptor = ArgumentCaptor.forClass(Music.class);

        when(jsonConverter.convert(payload, MusicDownloadedEvent.class))
                .thenReturn(musicDownloadedEvent);
        when(userService.findById(userId))
                .thenReturn(user);

        downloadMusicCommandHandler.handle(payload);

        verify(musicRepository).save(argumentCaptor.capture());
        Music savedMusic = argumentCaptor.getValue();
        assertEquals(music, savedMusic);
    }

    @Test
    void handle_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        String payload = "payload";
        Long userId = 1L;
        MusicDownloadedEvent musicDownloadedEvent = new MusicDownloadedEvent(userId, 2L, "videoId", "musicUrl", "uploader", "title");

        when(jsonConverter.convert(payload, MusicDownloadedEvent.class))
                .thenReturn(musicDownloadedEvent);
        when(userService.findById(userId))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> downloadMusicCommandHandler.handle(payload)
        );

        verify(musicRepository, never()).save(any());
    }

    @Test
    void handle_WhenInvalidPayloadProvided_ShouldThrowBadPayloadException() {
        String payload = "payload";

        when(jsonConverter.convert(payload, MusicDownloadedEvent.class))
                .thenThrow(new BadPayloadException());

        assertThrows(
                BadPayloadException.class,
                () -> downloadMusicCommandHandler.handle(payload)
        );

        verify(userService, never()).findById(any());
        verify(musicRepository, never()).save(any());
    }
}
