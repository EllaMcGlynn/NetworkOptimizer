package com.leea.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leea.models.TrafficData;
import com.leea.repo.DataRepo;

@Service
public class KafkaConsumerService {
	@Autowired
	private DataRepo repository;

	@KafkaListener(topics = "resource-usage-data", groupId = "consumer-group")
	public void listen(String message) throws JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    TrafficData data = mapper.readValue(message, TrafficData.class);
	    repository.save(data);
	}
}
