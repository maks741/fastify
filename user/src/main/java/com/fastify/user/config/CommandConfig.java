package com.fastify.user.config;

import com.fastify.user.model.command.MusicCommand;
import com.fastify.user.model.command.UserCommand;
import com.fastify.user.service.handler.CreateUserCommandHandler;
import com.fastify.user.service.handler.DownloadMusicCommandHandler;
import com.fastify.user.service.handler.UnknownCommandHandler;
import com.fastify.user.service.handler.CommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandConfig {

    @Bean
    public Map<UserCommand, CommandHandler> userCommandHandlerMap(
            CreateUserCommandHandler createUserCommandHandler,
            UnknownCommandHandler unknownCommandHandler
    ) {
        return Map.of(
                UserCommand.CREATE, createUserCommandHandler,
                UserCommand.UNKNOWN, unknownCommandHandler
        );
    }

    @Bean
    public Map<MusicCommand, CommandHandler> musicCommandHandlerMap(
            DownloadMusicCommandHandler downloadMusicCommandHandler,
            UnknownCommandHandler unknownCommandHandler
    ) {
        return Map.of(
                MusicCommand.DOWNLOAD, downloadMusicCommandHandler,
                MusicCommand.UNKNOWN, unknownCommandHandler
        );
    }
}
