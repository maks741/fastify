package com.fastify.auth.service;

import com.fastify.auth.controller.kafka.UserCommandProducer;
import com.fastify.auth.exception.DuplicateEmailException;
import com.fastify.auth.exception.NotFoundException;
import com.fastify.auth.exception.PublishingException;
import com.fastify.auth.model.command.UserCommand;
import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;
import com.fastify.auth.model.entity.User;
import com.fastify.auth.model.enumeration.Role;
import com.fastify.auth.model.event.UserCreatedEvent;
import com.fastify.auth.repository.UserRepository;
import com.fastify.auth.security.JwtService;
import com.fastify.auth.util.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    void signUp_WhenValidSignUpDetailsProvided_ShouldSignUpSuccessfully() {
        Long savedUserId = 2L;
        String username = "username";
        String email = "email@email.com";
        String password = "password";
        String encodedPassword = "encoded password";
        Role userRole = Role.USER;
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(username, email, password);
        User expectedSavedUser = User.builder()
                .id(savedUserId)
                .name(username)
                .email(email)
                .password(encodedPassword)
                .role(userRole)
                .build();
        User expectedUserToSave = User.builder()
                .name(username)
                .email(email)
                .password(encodedPassword)
                .role(userRole)
                .build();
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(
                savedUserId,
                email,
                userRole
        );
        String userCreatedEventJson = "json";
        String jwt = "jwt";
        SignUpResponseDto expectedResult = new SignUpResponseDto(savedUserId, jwt, username, email);

        when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
        when(userRepository.save(expectedUserToSave))
                .thenReturn(expectedSavedUser);
        when(jsonConverter.toJson(userCreatedEvent))
                .thenReturn(userCreatedEventJson);
        when(jwtService.generateToken(expectedSavedUser))
                .thenReturn(jwt);

        SignUpResponseDto actualResult = userService.signUp(signUpRequestDto);

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(expectedUserToSave);
        verify(jsonConverter).toJson(userCreatedEvent);
        verify(userCommandProducer)
                .sendUserCommand(userCommandTopic, savedUserId, userCreatedEventJson, UserCommand.CREATE);
        verify(jwtService).generateToken(expectedSavedUser);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void signUp_WhenUserCommandProducerThrowsPublishingException_ShouldThrowPublishingException() {
        Long savedUserId = 2L;
        String username = "username";
        String email = "email@email.com";
        String password = "password";
        String encodedPassword = "encoded password";
        Role userRole = Role.USER;
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(username, email, password);
        User expectedSavedUser = User.builder()
                .id(savedUserId)
                .name(username)
                .email(email)
                .password(encodedPassword)
                .role(userRole)
                .build();
        User expectedUserToSave = User.builder()
                .name(username)
                .email(email)
                .password(encodedPassword)
                .role(userRole)
                .build();
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(
                savedUserId,
                email,
                userRole
        );
        String userCreatedEventJson = "json";

        when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
        when(userRepository.save(expectedUserToSave))
                .thenReturn(expectedSavedUser);
        when(jsonConverter.toJson(userCreatedEvent))
                .thenReturn(userCreatedEventJson);
        doThrow(new PublishingException())
                .when(userCommandProducer).sendUserCommand(userCommandTopic, savedUserId, userCreatedEventJson, UserCommand.CREATE);

        assertThrows(
                PublishingException.class,
                () -> userService.signUp(signUpRequestDto)
        );

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(expectedUserToSave);
        verify(jsonConverter).toJson(userCreatedEvent);
        verify(userCommandProducer)
                .sendUserCommand(userCommandTopic, savedUserId, userCreatedEventJson, UserCommand.CREATE);
        verify(jwtService, never()).generateToken(expectedSavedUser);
    }

    @Test
    void signUp_WhenUserWithGivenEmailAlreadyExists_ShouldThrowDuplicateEmailException() {
        String username = "username";
        String email = "email@email.com";
        String password = "password";
        String encodedPassword = "encoded password";
        Role userRole = Role.USER;
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(username, email, password);
        User expectedUserToSave = User.builder()
                .name(username)
                .email(email)
                .password(encodedPassword)
                .role(userRole)
                .build();

        when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
        when(userRepository.save(expectedUserToSave))
                .thenThrow(new DataIntegrityViolationException(""));

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.signUp(signUpRequestDto)
        );

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(expectedUserToSave);
        verify(jsonConverter, never()).toJson(any());
        verify(userCommandProducer, never())
                .sendUserCommand(any(), any(), any(), any());
        verify(jwtService, never()).generateToken(any());
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
