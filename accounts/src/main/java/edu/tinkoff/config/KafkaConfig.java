package edu.tinkoff.config;

import edu.tinkoff.dto.Action;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {
    private final String servers;

    public KafkaConfig(
            @Value("${spring.kafka.bootstrap-servers}")
            String servers
    ) {
        this.servers = servers;
    }

    @Bean
    public KafkaTemplate<String, Action> actionKafkaTemplate() {
        return new KafkaTemplate<>(actionProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Action> actionProducerFactory() {
        Map<String, Object> props = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Action>>
    actionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Action> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(actionConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Action> actionConsumerFactory() {
        Map<String, Object> props = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Action.class));
    }
}
