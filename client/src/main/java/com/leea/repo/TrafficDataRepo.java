package com.leea.repo;

import com.leea.models.TrafficData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrafficDataRepo extends JpaRepository<TrafficData, Long> {

    List<TrafficData> findByNodeId(Integer nodeId);

    List<TrafficData> findByNetworkId(Integer networkId);

    List<TrafficData> findByNodeIdAndNetworkId(Integer nodeId, Integer networkId);

    List<TrafficData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM TrafficData t WHERE t.nodeId = :nodeId AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<TrafficData> findRecentByNodeId(@Param("nodeId") Integer nodeId, @Param("since") LocalDateTime since);

    @Query("SELECT t FROM TrafficData t ORDER BY t.timestamp DESC")
    Page<TrafficData> findAllOrderByTimeStampDesc(Pageable pageable);

    @Query("SELECT t FROM TrafficData t WHERE t.nodeId = :nodeId ORDER BY t.timestamp DESC")
    List<TrafficData> findTopByNodeIdOrderByTimestampDesc(@Param("nodeId") Integer nodeId, Pageable pageable);

    List<TrafficData> findTop30ByOrderByTimestampDesc();


}
