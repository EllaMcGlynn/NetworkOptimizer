package com.leea.optimizer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.optimizer.models.OptimizerAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActionKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ActionKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAction(OptimizerAction action) {
        try {
            System.out.println("Sending action " + action.toString());
            String json = objectMapper.writeValueAsString(action);
            System.out.println("Sending action: " + json);
            kafkaTemplate.send("optimizer-actions", json);
            System.out.println("Suggestion Sent");
        } catch (Exception e) {
            System.err.println("Failed to serialize action: " + e.getMessage());
        }
    }
}