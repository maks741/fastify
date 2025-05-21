package com.fastify.auth.service;

import com.fastify.auth.controller.kafka.UserCommandProducer;
import com.fastify.auth.exception.NotFoundException;
import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.entity.User;
import com.fastify.auth.repository.UserRepository;
import com.fastify.auth.security.JwtService;
import com.fastify.auth.util.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserCommandProducer userCommandProducer;

    @Mock
    JsonConverter jsonConverter;

    String userCommandTopic = "test-topic";

    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                jwtService,
                passwordEncoder,
                authenticationManager,
                userCommandProducer,
                jsonConverter,
                userCommandTopic
        );
    }

    @Test
    void login_WhenValidLoginProvided_ShouldCompleteSuccessfully() {
        String email = "email";
        String password = "password";
        String jwt = "jwt";
        String userName = "name";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setName(userName);
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);
        LoginResponseDto loginResponseDto = new LoginResponseDto(
                userId,
                jwt,
                userName,
                email
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(user))
                .thenReturn(jwt);

        LoginResponseDto actualResult = userService.login(loginRequestDto);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(email);
        verify(jwtService).generateToken(user);
        assertEquals(loginResponseDto, actualResult);
    }

    @Test
    void login_WhenAuthenticationExceptionThrown_ShouldThrowAuthenticationException() {
        String email = "email";
        String password = "password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("") {});

        assertThrows(
                AuthenticationException.class,
                () -> userService.login(loginRequestDto)
        );

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WhenUserNotFound_ThrowsNotFoundException() {
        String email = "email";
        String password = "password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> userService.login(loginRequestDto)
        );

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(email);
        verify(jwtService, never()).generateToken(any());
    }
}
