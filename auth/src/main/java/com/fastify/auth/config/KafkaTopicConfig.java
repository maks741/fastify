package com.fastify.auth.config;

import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
// FOR IT TO WORK VERY IMPORTANT TO HAVE A SETTER HERE
@Setter
public class KafkaTopicConfig {

    // It will go and read spring.kafka.topics property
    // and map the values of that property to that list
    private List<TopicConfig> topics;

    @Bean
    public List<NewTopic> createTopics() {
        return topics.stream()
                .map(topic -> new NewTopic(topic.name, topic.partitions, topic.replicationFactor))
                .toList();
    }

    record TopicConfig(String name, int partitions, short replicationFactor) {}
}
