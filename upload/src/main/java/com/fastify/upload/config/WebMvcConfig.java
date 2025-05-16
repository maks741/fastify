package com.fastify.upload.config;

import com.fastify.upload.annotation.resolver.UserArgumentResolver;
import com.fastify.upload.annotation.resolver.UserClaimsArgumentResolver;
import com.fastify.upload.security.JwtService;
import com.fastify.upload.service.UserService;
import lombok.RequiredArgsConstructor;
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
