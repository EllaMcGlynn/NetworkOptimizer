package com.leea.optimizer.kafka;

import com.leea.optimizer.models.OptimizerAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActionKafkaProducer {

    @Autowired
    private KafkaTemplate<String, OptimizerAction> kafkaTemplate;

    public void sendAction(OptimizerAction action) {
        kafkaTemplate.send("optimizer-actions", action);
    }
}
