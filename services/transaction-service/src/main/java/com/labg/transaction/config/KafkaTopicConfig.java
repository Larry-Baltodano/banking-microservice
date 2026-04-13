package com.labg.transaction.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private static final String TOPIC_COMPLETADAS = "transacciones-completadas";
    private static final String TOPIC_FALLIDAS = "transacciones-fallidas";
    private static final String TOPIC_REVERSOS = "transacciones-reverso-requerido";

    @Bean
    public NewTopic transaccionesCompletadasTopic() {
        return TopicBuilder.name(TOPIC_COMPLETADAS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transaccionesFallidasTopic() {
        return TopicBuilder.name(TOPIC_FALLIDAS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reversosRequeridosTopic() {
        return TopicBuilder.name(TOPIC_REVERSOS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
