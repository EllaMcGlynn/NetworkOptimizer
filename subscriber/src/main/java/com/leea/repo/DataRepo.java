package com.leea.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leea.models.TrafficData;

public interface DataRepo extends JpaRepository<TrafficData, Long> {
}
