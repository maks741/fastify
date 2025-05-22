package com.fastify.user.controller.kafka.consumer;

import com.fastify.user.model.command.MusicCommand;
import com.fastify.user.service.handler.DownloadMusicCommandHandler;
import com.fastify.user.service.handler.UnknownCommandHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest
public class MusicCommandListenerTest {

    @MockitoSpyBean
    DownloadMusicCommandHandler downloadMusicCommandHandler;

    @MockitoSpyBean
    UnknownCommandHandler unknownCommandHandler;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    Environment environment;

    String topic = "music-command";
    String commandHeader = "command";
    Long awaitSeconds;

    @BeforeEach
    void setUp() {
        awaitSeconds = environment.getProperty("await.seconds", Long.class);
    }

    @Test
    @SneakyThrows
    void consumeMusicCommand_WhenDownloadCommandProvided_ShouldInvokeDownloadHandler() {
        String payload = "payload";
        MusicCommand musicCommand = MusicCommand.DOWNLOAD;

        Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(commandHeader, musicCommand.name())
                .build();

        kafkaTemplate.send(message);

        await().atMost(Duration.ofSeconds(awaitSeconds)).untilAsserted(
                () -> verify(downloadMusicCommandHandler).handle(payload)
        );
    }

    @Test
    @SneakyThrows
    void consumeMusicCommand_WhenUnknownCommandProvided_ShouldInvokeUnknownHandler() {
        String payload = "payload";
        MusicCommand musicCommand = MusicCommand.UNKNOWN;

        Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(commandHeader, musicCommand.name())
                .build();

        kafkaTemplate.send(message);

        await().atMost(Duration.ofSeconds(awaitSeconds)).untilAsserted(
                () -> verify(unknownCommandHandler).handle(payload)
        );
    }

    @Test
    @SneakyThrows
    void consumeMusicCommand_WhenInvalidCommandProvided_ShouldInvokeUnknownHandler() {
        String payload = "payload";
        String invalidCommand = "invalidCommand";

        Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(commandHeader, invalidCommand)
                .build();

        kafkaTemplate.send(message);

        await().atMost(Duration.ofSeconds(awaitSeconds)).untilAsserted(
                () -> verify(unknownCommandHandler).handle(payload)
        );
    }
}
