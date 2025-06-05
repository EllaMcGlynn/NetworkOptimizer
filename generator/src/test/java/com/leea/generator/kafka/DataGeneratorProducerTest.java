package com.leea.generator.kafka;

import com.leea.generator.model.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class DataGeneratorProducerTest {

    private KafkaTemplate<String, DataGenerator> kafkaTemplate;
    private DataGeneratorProducer producer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        producer = new DataGeneratorProducer(kafkaTemplate);
    }

    @Test
    void testSend_callsKafkaTemplateWithCorrectTopic() {
        // Given
        DataGenerator data = new DataGenerator();
        
        // When
        producer.send(data);
        
        // Then
        verify(kafkaTemplate).send("resource-usage-data", data);
    }
}