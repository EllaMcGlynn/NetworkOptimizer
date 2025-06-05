package com.leea.generator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActionConsumer {

    private final DataGeneratorService dataGeneratorService;
    private final ObjectMapper objectMapper;

    public ActionConsumer(DataGeneratorService dataGeneratorService, ObjectMapper objectMapper) {
        this.dataGeneratorService = dataGeneratorService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "optimizer-actions", groupId = "generator-group", containerFactory = "actionKafkaListenerContainerFactory")
    public void consume(String actionJson) {
        try {
            OptimizerAction action = objectMapper.readValue(actionJson, OptimizerAction.class);
            dataGeneratorService.applyOptimizerAction(action);
        } catch (Exception e) {
            System.err.println("Failed to deserialize action: " + e.getMessage());
        }
    }
}