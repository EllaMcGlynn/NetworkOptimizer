package com.leea.generator.kafka;

import com.leea.generator.model.DataGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class DataGeneratorProducerTest {

    @Test
    void testSend_callsKafkaTemplateWithCorrectTopic() throws IllegalAccessException, NoSuchFieldException {
        KafkaTemplate<String, DataGenerator> kafkaTemplate = mock(KafkaTemplate.class);
        DataGeneratorProducer producer = new DataGeneratorProducer(kafkaTemplate);

        // Inject topic manually (simulate @Value)
        Field field = DataGeneratorProducer.class.getDeclaredField("topic");
        field.setAccessible(true);
        field.set(producer, "test-topic");

        DataGenerator data = new DataGenerator();
        producer.send(data);

        verify(kafkaTemplate, times(1)).send("test-topic", data);
    }
}
