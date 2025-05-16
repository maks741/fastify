package com.fastify.download.config;

import com.fastify.download.model.command.UserCommand;
import com.fastify.download.service.handler.CreateUserCommandHandler;
import com.fastify.download.service.handler.UnknownUserCommandHandler;
import com.fastify.download.service.handler.UserCommandHandler;
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
