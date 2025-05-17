package com.fastify.user.config;

import com.fastify.user.model.command.UserCommand;
import com.fastify.user.service.handler.CreateUserCommandHandler;
import com.fastify.user.service.handler.UnknownUserCommandHandler;
import com.fastify.user.service.handler.UserCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserCommandConfig {

    @Bean
    public Map<UserCommand, UserCommandHandler> userCommandHandlerMap(
            CreateUserCommandHandler createUserCommandHandler,
            UnknownUserCommandHandler unknownUserCommandHandler
    ) {
        return Map.of(
                UserCommand.CREATE, createUserCommandHandler,
                UserCommand.UNKNOWN, unknownUserCommandHandler
        );
    }
}
