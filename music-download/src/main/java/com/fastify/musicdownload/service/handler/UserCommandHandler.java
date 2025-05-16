package com.fastify.musicdownload.service.handler;

import com.fastify.musicdownload.model.command.UserCommand;

public interface UserCommandHandler {
    void handle(UserCommand userCommand);
}
