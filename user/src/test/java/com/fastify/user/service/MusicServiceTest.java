package com.fastify.user.service;

import com.fastify.user.exception.UserNotFoundException;
import com.fastify.user.model.dto.MusicDto;
import com.fastify.user.model.dto.user.UserClaims;
import com.fastify.user.model.entity.Music;
import com.fastify.user.model.entity.User;
import com.fastify.user.model.enumeration.Role;
import com.fastify.user.repository.MusicRepository;
import com.fastify.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    MusicRepository musicRepository;

    @Mock
    FileStorage fileStorage;

    @InjectMocks
    MusicService musicService;

    @Test
    void findAllByUserId_WhenUserIsFound_ShouldReturnUsersMusic() {
        Long userId = 1L;
        UserClaims userClaims = new UserClaims(userId, "email", Role.USER);
        User user = new User();
        Music music = new Music(1L, "videoId", "url", "uploader", "title", user);
        List<Music> musicList = List.of(music);
        String thumbnailUrl = "thumbnailUrl";
        List<MusicDto> expectedResult = List.of(
                new MusicDto(
                        music.getVideoId(),
                        music.getUrl(),
                        thumbnailUrl,
                        music.getUploader(),
                        music.getTitle()
                )
        );

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(musicRepository.findAllByUserId(userId))
                .thenReturn(musicList);
        when(fileStorage.generateThumbnailUrl(userClaims, music.getVideoId()))
                .thenReturn(thumbnailUrl);

        List<MusicDto> actualResult = musicService.findAllByUserId(userClaims);

        verify(userRepository).existsById(userId);
        verify(musicRepository).findAllByUserId(userId);
        verify(fileStorage).generateThumbnailUrl(userClaims, music.getVideoId());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAllByUserId_WhenUserIsNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        UserClaims userClaims = new UserClaims(userId, "email", Role.USER);

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> musicService.findAllByUserId(userClaims)
        );

        verify(userRepository).existsById(userId);
        verify(musicRepository, never()).findAllByUserId(any());
        verify(fileStorage, never()).generateThumbnailUrl(any(), any());
    }

}
