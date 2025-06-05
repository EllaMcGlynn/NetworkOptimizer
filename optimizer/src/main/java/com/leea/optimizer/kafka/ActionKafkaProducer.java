package com.leea.optimizer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.optimizer.models.OptimizerAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActionKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public ActionKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAction(OptimizerAction action) {
        try {
            String json = new ObjectMapper().writeValueAsString(action);
            kafkaTemplate.send("optimizer-actions", json);
            System.out.println("Suggestion Sent");
        } catch (Exception e) {
            System.err.println("Failed to serialize action: " + e.getMessage());
        }
    }


}

