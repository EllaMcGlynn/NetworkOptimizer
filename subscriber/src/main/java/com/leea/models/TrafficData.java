
package com.leea.models;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.*;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
public class TrafficData {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id = -1;
	private int nodeId, networkId;
	@ElementCollection
	@CollectionTable(name = "traffic_resource_usage", joinColumns = @JoinColumn(name = "traffic_data_id"))
	@MapKeyColumn(name = "resource_type")
	@Column(name = "usage_value")
	private Map<String, Double> resourceUsage;

	@ElementCollection
	@CollectionTable(name = "traffic_resource_allocated", joinColumns = @JoinColumn(name = "traffic_data_id"))
	@MapKeyColumn(name = "resource_type")
	@Column(name = "allocated_value")
	private Map<String, Double> resourceAllocated;


	private LocalDateTime timestamp;

	@JsonProperty("timestamp")
	public void setTimestamp(double epochSeconds) {
		this.timestamp = LocalDateTime.ofEpochSecond(
				(long) epochSeconds,
				(int) ((epochSeconds % 1) * 1_000_000_000),
				java.time.ZoneOffset.UTC
		);
	}

	public TrafficData() {

	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
