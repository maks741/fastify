package com.fastify.user.controller.kafka.consumer;

import com.fastify.user.model.command.UserCommand;
import com.fastify.user.service.handler.UserCommandHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserCommandListener {

    private final Map<UserCommand, UserCommandHandler> userCommandHandlerMap;
    private final String commandHeader;

    public UserCommandListener(
            Map<UserCommand, UserCommandHandler> userCommandHandlerMap,
            @Value("${spring.kafka.headers.command}") String commandHeader
    ) {
        this.userCommandHandlerMap = userCommandHandlerMap;
        this.commandHeader = commandHeader;
    }

    @KafkaListener(topics = "${spring.kafka.topics[0].name}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserCommand(ConsumerRecord<String, String> record) {
        var command = extractUserCommand(record);
        var handler = userCommandHandlerMap.get(command);
        handler.handle(record.value());
    }

    private UserCommand extractUserCommand(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(commandHeader);
        if (header == null) {
            return UserCommand.UNKNOWN;
        }
        return UserCommand.fromString(new String(header.value()));
    }
}
