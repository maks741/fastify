package com.fastify.musicdownload.service.handler;

import com.fastify.musicdownload.model.command.UserCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnknownUserCommandHandler implements UserCommandHandler {
    @Override
    public void handle(UserCommand userCommand) {
        log.warn("received unknown command");
    }
}
