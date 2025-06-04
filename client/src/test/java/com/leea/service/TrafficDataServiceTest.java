package com.leea.service;

import com.leea.dtos.ResourceUsageStats;
import com.leea.models.TrafficData;
import com.leea.repo.TrafficDataRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrafficDataServiceTest {

    @Mock
    private TrafficDataRepo trafficDataRepo;

    @InjectMocks
    private TrafficDataService trafficDataService;

    private TrafficData trafficData1;
    private TrafficData trafficData2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up some mock TrafficData objects with resource usage data
        Map<String, Double> resourceUsage1 = new HashMap<>();
        resourceUsage1.put("cpu", 30.0);
        resourceUsage1.put("memory", 60.0);
        resourceUsage1.put("bandwidth", 100.0);

        Map<String, Double> resourceAllocated1 = new HashMap<>();
        resourceAllocated1.put("cpu", 50.0);
        resourceAllocated1.put("memory", 100.0);
        resourceAllocated1.put("bandwidth", 200.0);

        trafficData1 = new TrafficData(1, 1, resourceUsage1, resourceAllocated1, LocalDateTime.now().minusHours(1));

        Map<String, Double> resourceUsage2 = new HashMap<>();
        resourceUsage2.put("cpu", 40.0);
        resourceUsage2.put("memory", 70.0);
        resourceUsage2.put("bandwidth", 120.0);

        Map<String, Double> resourceAllocated2 = new HashMap<>();
        resourceAllocated2.put("cpu", 60.0);
        resourceAllocated2.put("memory", 120.0);
        resourceAllocated2.put("bandwidth", 240.0);

        trafficData2 = new TrafficData(1, 1, resourceUsage2, resourceAllocated2, LocalDateTime.now().minusMinutes(30));
    }

    @Test
    void testGetAllTrafficData() {
        // Given
        when(trafficDataRepo.findAll()).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        List<TrafficData> result = trafficDataService.getAllTrafficData();

        // Then
        assertEquals(2, result.size());
        verify(trafficDataRepo, times(1)).findAll();
    }

    @Test
    void testGetTrafficDataById() {
        // Given
        Long id = 1L;
        when(trafficDataRepo.findById(id)).thenReturn(Optional.of(trafficData1));

        // When
        Optional<TrafficData> result = trafficDataService.getTrafficDataById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(trafficData1, result.get());
        verify(trafficDataRepo, times(1)).findById(id);
    }

    @Test
    void testGetTrafficDataByNode() {
        // Given
        Integer nodeId = 1;
        when(trafficDataRepo.findByNodeId(nodeId)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        List<TrafficData> result = trafficDataService.getTrafficDataByNode(nodeId);

        // Then
        assertEquals(2, result.size());
        verify(trafficDataRepo, times(1)).findByNodeId(nodeId);
    }

    @Test
    void testGetTrafficDataInTimeRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now();
        when(trafficDataRepo.findByTimestampBetween(start, end)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        List<TrafficData> result = trafficDataService.getTrafficDataInTimeRange(start, end);

        // Then
        assertEquals(2, result.size());
        verify(trafficDataRepo, times(1)).findByTimestampBetween(start, end);
    }

    @Test
    void testGetResourceUsageStats() {
        // Given
        Integer nodeId = 1;
        when(trafficDataRepo.findByNodeId(nodeId)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        ResourceUsageStats result = trafficDataService.getResourceUsageStats(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(35.0, result.getAvgCpuUsage());
        assertEquals(65.0, result.getAvgMemoryUsage());
        assertEquals(110.0, result.getAvgBandwidthUsage());
        assertEquals(40.0, result.getMaxCpuUsage());
        assertEquals(70.0, result.getMaxMemoryUsage());
        assertEquals(120.0, result.getMaxBandwidthUsage());
    }

    @Test
    void testGetCpuStats() {
        // Given
        Integer nodeId = 1;
        when(trafficDataRepo.findByNodeId(nodeId)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        ResourceUsageStats result = trafficDataService.getCpuStats(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(35.0, result.getAvgCpuUsage());
        assertEquals(40.0, result.getMaxCpuUsage());
        assertEquals(0.0, result.getAvgMemoryUsage());
        assertEquals(0.0, result.getMaxMemoryUsage());
        assertEquals(0.0, result.getAvgBandwidthUsage());
        assertEquals(0.0, result.getMaxBandwidthUsage());
    }

    @Test
    void testGetMemoryStats() {
        // Given
        Integer nodeId = 1;
        when(trafficDataRepo.findByNodeId(nodeId)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        ResourceUsageStats result = trafficDataService.getMemoryStats(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAvgCpuUsage());
        assertEquals(0.0, result.getMaxCpuUsage());
        assertEquals(65.0, result.getAvgMemoryUsage());
        assertEquals(70.0, result.getMaxMemoryUsage());
        assertEquals(0.0, result.getAvgBandwidthUsage());
        assertEquals(0.0, result.getMaxBandwidthUsage());
    }

    @Test
    void testGetBandwidthStats() {
        // Given
        Integer nodeId = 1;
        when(trafficDataRepo.findByNodeId(nodeId)).thenReturn(Arrays.asList(trafficData1, trafficData2));

        // When
        ResourceUsageStats result = trafficDataService.getBandwidthStats(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAvgCpuUsage());
        assertEquals(0.0, result.getMaxCpuUsage());
        assertEquals(0.0, result.getAvgMemoryUsage());
        assertEquals(0.0, result.getMaxMemoryUsage());
        assertEquals(110.0, result.getAvgBandwidthUsage());
        assertEquals(120.0, result.getMaxBandwidthUsage());
    }

    @Test
    void testGetAvailableResourceTypes() {
        // Given
        List<String> expected = Arrays.asList("CPU", "Memory", "Bandwidth");

        // When
        List<String> result = trafficDataService.getAvailableResourceTypes();

        // Then
        assertEquals(expected, result);
    }

    @Test
    void testGetTrafficDataPageable() {
        // Given
        int page = 0;
        int size = 10;
        Page<TrafficData> trafficDataPage = new PageImpl<>(Arrays.asList(trafficData1, trafficData2));
        when(trafficDataRepo.findAllOrderByTimeStampDesc(PageRequest.of(page, size))).thenReturn(trafficDataPage);

        // When
        Page<TrafficData> result = trafficDataService.getTrafficData(page, size);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(trafficDataRepo, times(1)).findAllOrderByTimeStampDesc(PageRequest.of(page, size));
    }
}
