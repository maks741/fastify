package com.fastify.musicdownload.config;

import com.fastify.musicdownload.annotation.resolver.UserArgumentResolver;
import com.fastify.musicdownload.annotation.resolver.UserClaimsArgumentResolver;
import com.fastify.musicdownload.security.JwtService;
import com.fastify.musicdownload.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserArgumentResolver(jwtService, userService));
        resolvers.add(new UserClaimsArgumentResolver(jwtService));
    }
}
