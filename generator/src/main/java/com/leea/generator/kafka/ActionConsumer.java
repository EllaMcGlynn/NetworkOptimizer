package com.leea.generator.kafka;

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

    @KafkaListener(topics = "optimizer-actions", groupId = "generator-group", containerFactory = "actionKafkaListenerContainerFactory")
    public void consume(OptimizerAction action) {
        dataGeneratorService.applyOptimizerAction(action);
    }
}
