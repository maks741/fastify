package com.fastify.auth.controller;

import com.fastify.auth.exception.DuplicateEmailException;
import com.fastify.auth.exception.handler.CustomExceptionHandler;
import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.service.UserService;
import com.fastify.auth.util.JsonConverter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    AuthController authController;

    MockMvc mockMvc;
    String baseUrl = "/auth";
    JsonConverter jsonConverter = new JsonConverter();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    @SneakyThrows
    void singUp_WhenGivenValidSignUpDto_ShouldCallUserService() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "username",
                "email@email.com",
                "password"
        );
        String signUpRequestDtoJson = jsonConverter.toJson(signUpRequestDto);

        mockMvc.perform(post(baseUrl + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequestDtoJson))
                .andExpect(status().isCreated());

        verify(userService).signUp(signUpRequestDto);
    }

    @Test
    @SneakyThrows
    void signUp_WhenGivenInvalidSignUpDto_ShouldReturnBadRequest() {
        SignUpRequestDto invalidSignUpRequestDto = new SignUpRequestDto(
                "username",
                "invalidEmail",
                "password"
        );
        String signUpRequestDtoJson = jsonConverter.toJson(invalidSignUpRequestDto);

        mockMvc.perform(post(baseUrl + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestDtoJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).signUp(any());
    }

    @Test
    @SneakyThrows
    void signUp_WhenUserServiceThrowsDuplicateEmailException_ShouldReturnConflict() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "username",
                "email@email.com",
                "password"
        );
        String signUpRequestDtoJson = jsonConverter.toJson(signUpRequestDto);

        when(userService.signUp(signUpRequestDto))
                .thenThrow(new DuplicateEmailException());

        mockMvc.perform(post(baseUrl + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestDtoJson))
                .andExpect(status().isConflict());

        verify(userService).signUp(signUpRequestDto);
    }

    @Test
    @SneakyThrows
    void login_WhenGivenValidLoginDto_ShouldCallUserService() {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "email@email.com",
                "password"
        );
        String loginRequestDtoJson = jsonConverter.toJson(loginRequestDto);

        mockMvc.perform(post(baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isOk());

        verify(userService).login(loginRequestDto);
    }

    @Test
    @SneakyThrows
    void login_WhenGivenInvalidLoginDto_ShouldReturnBadRequest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "invalidEmail",
                "password"
        );
        String loginRequestDtoJson = jsonConverter.toJson(loginRequestDto);

        mockMvc.perform(post(baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(loginRequestDto);
    }

    @Test
    @SneakyThrows
    void login_WhenUserServiceThrowsAuthenticationException_ShouldReturnUnauthorized() {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "email@email.com",
                "password"
        );
        String loginRequestDtoJson = jsonConverter.toJson(loginRequestDto);

        when(userService.login(loginRequestDto))
                .thenThrow(new AuthenticationException("") {});

        mockMvc.perform(post(baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestDtoJson))
                .andExpect(status().isUnauthorized());

        verify(userService).login(loginRequestDto);
    }
}
