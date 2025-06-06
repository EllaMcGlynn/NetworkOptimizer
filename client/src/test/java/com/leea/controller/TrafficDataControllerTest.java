package com.leea.controller;

import com.leea.models.TrafficData;
import com.leea.service.TrafficDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrafficDataController.class)
@AutoConfigureMockMvc
class TrafficDataControllerTest {

    @Configuration
    @EnableWebMvc
    static class TestConfig {
        @Bean
        public TrafficDataService trafficDataService() {
            return mock(TrafficDataService.class);
        }

        @Bean
        public TrafficDataController trafficDataController(TrafficDataService trafficDataService) {
            return new TrafficDataController(trafficDataService);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrafficDataService trafficDataService;

    @Test
    void testGetAllTrafficData() throws Exception {
        TrafficData dummy = new TrafficData();
        List<TrafficData> content = new ArrayList<>();
        content.add(dummy);
        PageImpl<TrafficData> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);
        when(trafficDataService.getTrafficData(eq(0), eq(10))).thenReturn(page);

        mockMvc.perform(get("/traffic")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTrafficDataById_found() throws Exception {
        TrafficData dummy = new TrafficData();
        dummy.setId(1L);
        when(trafficDataService.getTrafficDataById(1L)).thenReturn(Optional.of(dummy));

        mockMvc.perform(get("/traffic/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTrafficDataById_notFound() throws Exception {
        when(trafficDataService.getTrafficDataById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/traffic/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTrafficDataByNode() throws Exception {
        when(trafficDataService.getTrafficDataByNode(5)).thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/node/5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTrafficDataInTimeRange() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(trafficDataService.getTrafficDataInTimeRange(any(), any()))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/timerange")
                .param("start", start.toString())
                .param("end", end.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAvailableResourceTypes() throws Exception {
        when(trafficDataService.getAvailableResourceTypes())
                .thenReturn(List.of("CPU", "Memory"));

        mockMvc.perform(get("/traffic/resource-types")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("CPU"))
                .andExpect(jsonPath("$[1]").value("Memory"));
    }

    @Test
    void testGetTrafficDataByNetwork() throws Exception {
        when(trafficDataService.getTrafficDataByNetwork(42))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/network/42")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTrafficDataByNodeAndNetwork() throws Exception {
        when(trafficDataService.getTrafficDataByNodeAndNetwork(7, 13))
                .thenReturn(List.of(new TrafficData()));

        mockMvc.perform(get("/traffic/node/7/network/13")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}