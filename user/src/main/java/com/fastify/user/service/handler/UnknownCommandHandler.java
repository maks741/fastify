package com.fastify.user.service.handler;

import com.fastify.user.exception.UnknownCommandException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnknownCommandHandler implements CommandHandler {
    @Override
    public void handle(String payload) {
        throw new UnknownCommandException("Received unknown command");
    }
}
