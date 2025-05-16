package com.fastify.download.config;

import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Setter
public class KafkaTopicConfig {

    private List<TopicConfig> topics;

    @Bean
    public List<NewTopic> createTopics() {
        return topics.stream()
                .map(topic -> new NewTopic(topic.name, topic.partitions, topic.replicationFactor))
                .toList();
    }

    record TopicConfig(String name, int partitions, short replicationFactor) {}
}
