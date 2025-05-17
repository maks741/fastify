package com.fastify.user.controller.kafka.consumer;

import com.fastify.user.model.command.MusicCommand;
import com.fastify.user.service.handler.CommandHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MusicCommandListener {

    private final Map<MusicCommand, CommandHandler> musicCommandHandlerMap;
    private final String commandHeader;

    public MusicCommandListener(
            Map<MusicCommand, CommandHandler> musicCommandHandlerMap,
            @Value("${spring.kafka.headers.command}") String commandHeader
    ) {
        this.musicCommandHandlerMap = musicCommandHandlerMap;
        this.commandHeader = commandHeader;
    }

    @KafkaListener(topics = "${spring.kafka.topics[1].name}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeMusicCommand(ConsumerRecord<String, String> record) {
        var command = extractMusicCommand(record);
        var handler = musicCommandHandlerMap.get(command);
        handler.handle(record.value());
    }

    private MusicCommand extractMusicCommand(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(commandHeader);
        if (header == null) {
            return MusicCommand.UNKNOWN;
        }
        return MusicCommand.fromString(new String(header.value()));
    }
}
