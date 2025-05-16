package com.fastify.upload.config;

import com.fastify.upload.model.command.UserCommand;
import com.fastify.upload.service.handler.CreateUserCommandHandler;
import com.fastify.upload.service.handler.UnknownUserCommandHandler;
import com.fastify.upload.service.handler.UserCommandHandler;
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
