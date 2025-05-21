package com.fastify.auth.controller.kafka;

import com.fastify.auth.exception.PublishingException;
import com.fastify.auth.model.command.UserCommand;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 3, count = 3)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
class UserCommandProducerTest {

    @Autowired
    UserCommandProducer userCommandProducer;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    Environment environment;

    KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer;
    BlockingQueue<ConsumerRecord<String, String>> blockingQueue;
    String commandHeader;

    @BeforeAll
    void setUp() {
        var consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties());
        var containerProperties = new ContainerProperties(environment.getProperty("spring.kafka.topics[0].name"));
        kafkaMessageListenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        blockingQueue = new LinkedBlockingDeque<>();

        kafkaMessageListenerContainer.setupMessageListener((MessageListener<String, String>) blockingQueue::add);
        kafkaMessageListenerContainer.start();

        ContainerTestUtils.waitForAssignment(kafkaMessageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());

        commandHeader = environment.getProperty("spring.kafka.headers.command");
    }

    @AfterAll
    void tearDown() {
        kafkaMessageListenerContainer.stop();
    }

    @Test
    @SneakyThrows
    void sendUserCommand_WhenValidParamsProvided_successfullySendsMessage() {
        String topic = environment.getProperty("spring.kafka.topics[0].name");
        Long userId = 1L;
        String payload = "payload";
        UserCommand userCommand = UserCommand.CREATE;

        userCommandProducer.sendUserCommand(topic, userId, payload, userCommand);

        ConsumerRecord<String, String> producedRecord = blockingQueue.poll(20000, TimeUnit.MILLISECONDS);
        assertNotNull(producedRecord);
        assertNotNull(producedRecord.key());
        assertNotNull(producedRecord.value());

        String actualPayload = producedRecord.value();
        assertEquals(payload, actualPayload);
        assertEquals(topic, producedRecord.topic());
        assertEquals(userId, producedRecord.key());
        assertEquals(userCommand.name(), new String(producedRecord.headers().lastHeader(commandHeader).value()));
    }

    @Test
    @SneakyThrows
    void sendUserCommand_WhenKafkaTemplateThrowsException_throwsPublishingException() {
        String topic = environment.getProperty("spring.kafka.topics[0].name");
        Long userId = 1L;
        String payload = "payload";
        UserCommand userCommand = UserCommand.CREATE;

        when(kafkaTemplate.send(any(Message.class)))
                .thenThrow(new RuntimeException());

        assertThrows(
                PublishingException.class,
                () -> userCommandProducer.sendUserCommand(topic, userId, payload, userCommand)
        );

        ConsumerRecord<String, String> producedRecord = blockingQueue.poll(5000, TimeUnit.MILLISECONDS);
        assertNull(producedRecord);
    }

    Map<String, Object> consumerProperties() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset"));

        return config;
    }
}
