package com.fastify.musicdownload.service.handler;

import com.fastify.musicdownload.exception.UnknownCommandException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnknownUserCommandHandler implements UserCommandHandler {
    @Override
    public void handle(String payload) {
        throw new UnknownCommandException("Received unknown command");
    }
}
