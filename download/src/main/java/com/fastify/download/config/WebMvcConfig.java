package com.fastify.download.config;

import com.fastify.download.annotation.resolver.UserArgumentResolver;
import com.fastify.download.security.JwtService;
import com.fastify.download.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtService jwtService;
    private final UserService userService;
    private final String storagePath;

    public WebMvcConfig(
            JwtService jwtService,
            UserService userService,
            @Value("${download.storage.path}") String storagePath
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.storagePath = storagePath;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserArgumentResolver(jwtService, userService));
    }

    // TODO: make separate prod/dev branches and have it only in dev branch
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry
                .addResourceHandler("/resources/**")
                .addResourceLocations("file:" + storagePath)
                .setCachePeriod(0);
    }
}
