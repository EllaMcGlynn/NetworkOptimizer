package com.leea.optimizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leea.optimizer.models.TrafficData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.leea.optimizer.service.OptimizationService;

@Service
public class KafkaTrafficDataConsumer {

	private final List<TrafficData> recentData = new CopyOnWriteArrayList<>();

	@KafkaListener(topics = "resource-usage-data", groupId = "optimizer-group")
	public void listen(String message) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		TrafficData data = mapper.readValue(message, TrafficData.class);
		recentData.add(data);

		//System.out.print(data.toString());
		//System.out.print("Recieved data");

	}


	public List<TrafficData> getRecentData() {
		return recentData;
	}
}

