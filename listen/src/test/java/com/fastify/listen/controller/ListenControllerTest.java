package com.fastify.listen.controller;

import com.fastify.listen.annotation.resolver.UserClaimsArgumentResolver;
import com.fastify.listen.security.JwtService;
import com.fastify.listen.service.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ListenControllerTest {

    @Mock
    JwtService jwtService;

    @Mock
    FileStorage fileStorage;

    @InjectMocks
    ListenController listenController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(listenController)
                .setCustomArgumentResolvers(new UserClaimsArgumentResolver(jwtService))
                .build();
    }

    @Test
    void listen_WhenFileStorage

}
