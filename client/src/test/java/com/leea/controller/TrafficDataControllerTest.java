package com.leea.controller;

import com.leea.dtos.ResourceUsageStats;
import com.leea.models.TrafficData;
import com.leea.service.TrafficDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrafficDataController.class, useDefaultFilters = false)
@Import(TrafficDataController.class) 
public class TrafficDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficDataService trafficDataService;

    @Test
    void testGetAllTrafficData() throws Exception {
        TrafficData td = new TrafficData(); // Fill with dummy values as needed
        when(trafficDataService.getTrafficData(0, 10))
            .thenReturn(new PageImpl<>(List.of(td), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/traffic?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrafficDataByIdFound() throws Exception {
        TrafficData td = new TrafficData(); // dummy
        when(trafficDataService.getTrafficDataById(1L)).thenReturn(Optional.of(td));

        mockMvc.perform(get("/traffic/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrafficDataByIdNotFound() throws Exception {
        when(trafficDataService.getTrafficDataById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/traffic/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTrafficDataByNode() throws Exception {
        when(trafficDataService.getTrafficDataByNode(1)).thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/node/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrafficDataByNetwork() throws Exception {
        when(trafficDataService.getTrafficDataByNetwork(100)).thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/network/100"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrafficDataByNodeAndNetwork() throws Exception {
        when(trafficDataService.getTrafficDataByNodeAndNetwork(1, 100))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/node/1/network/100"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrafficDataInTimeRange() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(trafficDataService.getTrafficDataInTimeRange(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/timerange")
                .param("start", now.minusHours(1).toString())
                .param("end", now.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRecentTrafficDataByNode() throws Exception {
        when(trafficDataService.getRecentTrafficDataByNode(1, 24))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/node/1/recent"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAvailableResourceTypes() throws Exception {
        when(trafficDataService.getAvailableResourceTypes()).thenReturn(List.of("cpu", "memory"));

        mockMvc.perform(get("/traffic/resource-types"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetResourceStats() throws Exception {
        when(trafficDataService.getResourceUsageStats(1)).thenReturn(new ResourceUsageStats(70.5, 1500.0, 300.0, 70.5, 1500.0, 300.0));

        mockMvc.perform(get("/traffic/node/1/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCpuStats() throws Exception {
        when(trafficDataService.getCpuStats(1)).thenReturn(new ResourceUsageStats(70.5, 1500.0, 300.0, 70.5, 1500.0, 300.0));

        mockMvc.perform(get("/traffic/node/1/stats/cpu"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMemoryStats() throws Exception {
        when(trafficDataService.getMemoryStats(1)).thenReturn(new ResourceUsageStats(70.5, 1500.0, 300.0, 70.5, 1500.0, 300.0));

        mockMvc.perform(get("/traffic/node/1/stats/memory"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBandwidthStats() throws Exception {
        when(trafficDataService.getBandwidthStats(1)).thenReturn(new ResourceUsageStats(70.5, 1500.0, 300.0, 70.5, 1500.0, 300.0));

        mockMvc.perform(get("/traffic/node/1/stats/bandwidth"))
                .andExpect(status().isOk());
    }
}
