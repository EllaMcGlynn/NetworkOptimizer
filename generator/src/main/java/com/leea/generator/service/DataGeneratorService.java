package com.leea.generator.service;

import com.leea.generator.model.DataGenerator;
import com.leea.generator.kafka.DataGeneratorProducer;
import com.leea.generator.logging.NodeLogger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.*;

@Service
public class DataGeneratorService {
    private final DataGeneratorProducer dataGeneratorProducer;
    private final Random random = new Random();


    private final Map<Integer, Map<String, Double>> allocationMap = new HashMap<>();

    public DataGeneratorService(DataGeneratorProducer producer) {
        this.dataGeneratorProducer = producer;
        initAllocations();
    }

    private void initAllocations() {
        for (int i = 0; i < 5; i++) {
            allocationMap.put(i, Map.of(
                    "cpu", 80.0,
                    "memory", 2048.0,
                    "bandwith", 500.0
            ));
        }
    }

    @Scheduled(fixedRate = 2000)
    public void genAndSendData() {
        for ( int nodeId : allocationMap.keySet() ) {
            Map<String, Double> allocated = allocationMap.get(nodeId);
            Map<String, Double> usage = new HashMap<>();

            allocated.forEach((key, value) -> {
                double used = value * (0.5 + random.nextDouble() * 0.5);
                usage.put(key, Math.round(used * 10.0) / 10.0);
            });

            DataGenerator data = new DataGenerator();
            data.nodeId = nodeId;
            data.networkId = 100 + nodeId;
            data.resourceAllocated = allocated;
            data.resourceUsage = usage;
            data.timestamp = Instant.now();

            NodeLogger.log(data);

            dataGeneratorProducer.send(data);
        }
    }
}
