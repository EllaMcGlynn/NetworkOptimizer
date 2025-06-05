//package com.leea.optimizer.kafka;
//
//import com.leea.optimizer.models.OptimizerAction;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import static org.mockito.Mockito.*;
//
//public class ActionKafkaProducerTest {
//
//    @Test
//    public void testProducerSendsToCorrectTopic() {
//        KafkaTemplate<String, OptimizerAction> mockTemplate = mock(KafkaTemplate.class);
//        ActionKafkaProducer producer = new ActionKafkaProducer(mockTemplate);
//
//        OptimizerAction action = new OptimizerAction();
//        action.setNodeId(1);
//        action.setResourceType("CPU");
//
//        producer.sendAction(action);
//
//        verify(mockTemplate).send("optimizer-actions", action);
//    }
//}
