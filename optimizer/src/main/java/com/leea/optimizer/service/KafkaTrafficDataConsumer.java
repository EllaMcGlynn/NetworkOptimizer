package com.leea.optimizer.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leea.logger.OptimizerLogger;
import com.leea.optimizer.models.TrafficData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaTrafficDataConsumer {

    private final List<TrafficData> recentData = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "resource-usage-data", groupId = "optimizer-group")
    public void listen(String message) throws Exception {
    	try {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	        TrafficData data = mapper.readValue(message, TrafficData.class);
	        if (data.getResourceAllocated() == null || data.getResourceUsage() == null || data.getTimestamp() == null) {
		    	OptimizerLogger.logError("null value found entry for node " + data.getNodeId() + " skipped");
		    }else {
		    	recentData.add(data);
		    	OptimizerLogger.log(data);
		    }
    	}catch (JsonParseException  e) {
			e.printStackTrace();
		}
    }

    public List<TrafficData> getRecentData() {
        return recentData;
    }
}
