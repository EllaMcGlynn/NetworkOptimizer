package com.leea.models;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.Data;

@Entity
@Data
public class TrafficData {
	@Id
	private long id;
	private int nodeID, networkID;
	@ElementCollection
	@CollectionTable(name = "traffic_resource_usage", joinColumns = @JoinColumn(name = "traffic_data_id"))
	@MapKeyColumn(name = "resource_type")
	@Column(name = "value")
	private Map<String, Double> resourceUsage;

	@ElementCollection
	@CollectionTable(name = "traffic_resource_allocated", joinColumns = @JoinColumn(name = "traffic_data_id"))
	@MapKeyColumn(name = "resource_type")
	@Column(name = "value")
	private Map<String, Double> resourceAllocated;

	private LocalDateTime timeStamp;
	
	public TrafficData() {
		
	}
}
