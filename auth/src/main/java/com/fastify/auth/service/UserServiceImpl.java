package com.fastify.auth.service;

import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;
import com.fastify.auth.model.entity.User;
import com.fastify.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = User.builder()
                .username(signUpRequestDto.username())
                .email(signUpRequestDto.email())
                .build();

        user = userRepository.save(user);

        return new SignUpResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        return null;
    }
}
