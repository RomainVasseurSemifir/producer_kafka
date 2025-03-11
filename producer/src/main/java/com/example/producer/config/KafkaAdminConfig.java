package com.example.producer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        // creation d'un dictionaire de configuration
        Map<String, Object> configs = new HashMap<>();
        configs.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress
        );
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("app-topic")
                .replicas(3)
                .partitions(7)
                .build();
    }

    @Bean
    public KafkaAdmin.NewTopics topic23456(){
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("app-topic2")
                        .replicas(2)
                        .partitions(2)
                        .build(),
                TopicBuilder.name("app-topic3")
                        .replicas(2)
                        .partitions(3)
                        .build(),
                TopicBuilder.name("app-topic4")
                        .replicas(2)
                        .partitions(4)
                        .build()
        );
    }
}
