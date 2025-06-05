package com.leea.generator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OptimizerAction> actionKafkaListenerContainerFactory(
            ConsumerFactory<String, OptimizerAction> actionConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OptimizerAction> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(actionConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OptimizerAction> actionConsumerFactory(
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "generator-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        JsonDeserializer<OptimizerAction> deserializer =
                new JsonDeserializer<>(OptimizerAction.class, objectMapper, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

}

