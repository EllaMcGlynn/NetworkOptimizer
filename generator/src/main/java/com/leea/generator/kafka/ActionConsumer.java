package com.leea.generator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActionConsumer {

    private final DataGeneratorService dataGeneratorService;

    public ActionConsumer(DataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }

    @KafkaListener(topics = "optimizer-actions", groupId = "generator-group")
    public void consume(String json) {
        System.out.println("Received action JSON: " + json);
        try {
            ObjectMapper mapper = new ObjectMapper();
            OptimizerAction action = mapper.readValue(json, OptimizerAction.class);
            dataGeneratorService.applyOptimizerAction(action);
        } catch (Exception e) {
            System.err.println("Failed to deserialize action: " + e.getMessage());
        }
    }


}
