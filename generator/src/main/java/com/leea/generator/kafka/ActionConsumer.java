package com.leea.generator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActionConsumer {

    private final DataGeneratorService dataGeneratorService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActionConsumer(DataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }

    @KafkaListener(topics = "optimizer-actions", groupId = "generator-group", containerFactory = "actionKafkaListenerContainerFactory")

    public void consume(String actionJson) {
        //System.out.println("Received action JSON: " + actionJson);
        try {
            OptimizerAction action = objectMapper.readValue(actionJson, OptimizerAction.class);
            dataGeneratorService.applyOptimizerAction(action);
        } catch (Exception e) {
            System.err.println("Failed to deserialize action: " + e.getMessage());
        }
    }
}

