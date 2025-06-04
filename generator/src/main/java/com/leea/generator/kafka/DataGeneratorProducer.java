package com.leea.generator.kafka;

import com.leea.generator.model.DataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataGeneratorProducer {

    @Value("${app.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, DataGenerator> kafkaTemplate;

    public DataGeneratorProducer(KafkaTemplate<String, DataGenerator> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(DataGenerator data) {
        kafkaTemplate.send(topic, data);
    }

}
