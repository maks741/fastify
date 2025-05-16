package com.fastify.musicdownload.service.handler;

import com.fastify.musicdownload.model.command.UserCommand;
import org.springframework.stereotype.Service;

@Service
public class CreateUserCommandHandler implements UserCommandHandler {

    @Override
    public void handle(UserCommand userCommand) {

    }
}
