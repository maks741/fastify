package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.DuplicateDownloadException;
import com.fastify.musicdownload.exception.UserNotFoundException;
import com.fastify.musicdownload.model.entity.Music;
import com.fastify.musicdownload.model.entity.User;
import com.fastify.musicdownload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
    }

    public void addDownloadedMusic(User user, String musicUrl) {
        if (user.getPlaylist()
                .stream()
                .map(Music::getUrl)
                .toList().contains(musicUrl)) {
            throw new DuplicateDownloadException("Cannot download same video multiple times");
        }
        Music music = new Music(musicUrl, user);
        user.getPlaylist().add(music);

        userRepository.save(user);
    }
}
