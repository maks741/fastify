package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.UserNotFoundException;
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

}
