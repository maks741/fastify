package com.fastify.download.controller.kafka.producer;

import com.fastify.download.model.command.MusicCommand;
import com.fastify.download.model.event.MusicDownloadedEvent;
import com.fastify.download.util.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MusicCommandProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String commandHeader;
    private final String musicCommandTopic;
    private final JsonConverter jsonConverter;

    public MusicCommandProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${spring.kafka.headers.command}") String commandHeader,
            @Value("${spring.kafka.topics[1].name}") String musicCommandTopic,
            JsonConverter jsonConverter) {
        this.kafkaTemplate = kafkaTemplate;
        this.commandHeader = commandHeader;
        this.musicCommandTopic = musicCommandTopic;
        this.jsonConverter = jsonConverter;
    }

    public void sendMusicDownloadedEvent(MusicDownloadedEvent musicDownloadedEvent) {
        String payload = jsonConverter.toJson(musicDownloadedEvent);
        var message = buildMessage(musicDownloadedEvent.musicId(), payload, MusicCommand.DOWNLOAD);
        kafkaTemplate.send(message);
        log.info("Music command sent: {}", message);
    }

    private Message<String> buildMessage(Long musicId, String payload, MusicCommand musicCommand) {
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, musicCommandTopic)
                .setHeader(KafkaHeaders.KEY, musicId)
                .setHeader(commandHeader, musicCommand.name())
                .build();
    }
}
