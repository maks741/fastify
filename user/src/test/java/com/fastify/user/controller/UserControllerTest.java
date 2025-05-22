package com.fastify.user.controller;

import com.fastify.user.annotation.resolver.UserClaimsArgumentResolver;
import com.fastify.user.exception.UserNotFoundException;
import com.fastify.user.exception.handler.CustomExceptionHandler;
import com.fastify.user.model.dto.user.UserClaims;
import com.fastify.user.model.enumeration.Role;
import com.fastify.user.security.JwtService;
import com.fastify.user.service.MusicService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    JwtService jwtService;

    @Mock
    MusicService musicService;

    @InjectMocks
    UserController userController;

    MockMvc mockMvc;

    String baseUrl = "/users";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionHandler())
                .setCustomArgumentResolvers(new UserClaimsArgumentResolver(jwtService))
                .build();
    }

    @Test
    @SneakyThrows
    void findAllMusicOfUser_WhenUserIsFound_ShouldReturnAllUsersMusic() {
        UserClaims userClaims = new UserClaims(1L, "email", Role.USER);
        String jwt = "jwt";

        when(jwtService.extractJwt(any(NativeWebRequest.class)))
                .thenReturn(jwt);
        when(jwtService.extractUserClaims(jwt))
                .thenReturn(userClaims);

        mockMvc.perform(get(baseUrl + "/music"))
                        .andExpect(status().isOk());

        verify(musicService).findAllByUserId(userClaims);
    }

    @Test
    @SneakyThrows
    void findAllMusicOfUser_WhenUserIsNotFound_ShouldReturnNotFound() {
        UserClaims userClaims = new UserClaims(1L, "email", Role.USER);
        String jwt = "jwt";

        when(jwtService.extractJwt(any(NativeWebRequest.class)))
                .thenReturn(jwt);
        when(jwtService.extractUserClaims(jwt))
                .thenReturn(userClaims);
        when(musicService.findAllByUserId(userClaims))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get(baseUrl + "/music"))
                .andExpect(status().isNotFound());

        verify(musicService).findAllByUserId(userClaims);
    }
}
