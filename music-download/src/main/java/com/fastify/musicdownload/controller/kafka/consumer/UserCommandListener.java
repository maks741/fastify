package com.fastify.musicdownload.controller.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserCommandListener {

    @KafkaListener(topics = "${spring.kafka.topics[0].name}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserCommand(ConsumerRecord<String, String> consumerRecord) {

    }

}
