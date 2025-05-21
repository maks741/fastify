package com.fastify.auth.controller.kafka;

import com.fastify.auth.exception.PublishingException;
import com.fastify.auth.model.command.UserCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCommandProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String commandHeader;

    public UserCommandProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${spring.kafka.headers.command}") String commandHeader
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.commandHeader = commandHeader;
    }

    public void sendUserCommand(String topic, Long userId, String payload, UserCommand userCommand) throws PublishingException {
        var message = buildMessage(topic, userId, payload, userCommand);
        try {
            kafkaTemplate.send(message);
        } catch (Exception e) {
            throw new PublishingException("Message: " + message + " could not be sent: " + e.getMessage());
        }
        log.info("User command sent: {}", message);
    }

    private Message<String> buildMessage(String topic, Long userId, String payload, UserCommand userCommand) {
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, userId)
                .setHeader(commandHeader, userCommand.name())
                .build();
    }
}
