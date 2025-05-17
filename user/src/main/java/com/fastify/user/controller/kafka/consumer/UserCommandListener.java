package com.fastify.user.controller.kafka.consumer;

import com.fastify.user.model.command.UserCommand;
import com.fastify.user.service.handler.UserCommandHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserCommandListener {

    private final Map<UserCommand, UserCommandHandler> userCommandHandlerMap;
    private final String commandHeader;

}
