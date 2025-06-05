package com.leea.generator.kafka;

import com.leea.generator.model.DataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataGeneratorProducer {

    private static final String TOPIC = "resource-usage-data";

    private final KafkaTemplate<String, DataGenerator> kafkaTemplate;

    public DataGeneratorProducer(KafkaTemplate<String, DataGenerator> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(DataGenerator data) {
        //System.out.print("Sent data");
        kafkaTemplate.send(TOPIC, data);
    }
}