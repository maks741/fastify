package com.fastify.download.service;

import com.fastify.download.exception.DuplicateDownloadException;
import com.fastify.download.exception.UserNotFoundException;
import com.fastify.download.model.entity.Music;
import com.fastify.download.model.entity.User;
import com.fastify.download.repository.MusicRepository;
import com.fastify.download.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
    }

    public Music addDownloadedMusic(User user, String musicUrl) {
        if (user.getPlaylist()
                .stream()
                .map(Music::getUrl)
                .toList().contains(musicUrl)) {
            throw new DuplicateDownloadException("Cannot download same video multiple times");
        }
        Music music = new Music(musicUrl, user);
        music = musicRepository.save(music);
        user.getPlaylist().add(music);

        userRepository.save(user);

        return music;
    }
}
