package com.leea.controller;
import com.leea.models.TrafficData;
import com.leea.service.TrafficDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrafficDataControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TrafficDataService trafficDataService;

    private String baseUrl(String path) {
        return "http://localhost:" + port + "/traffic" + path;
    }

    @Test
    void testGetAllTrafficData() {
        TrafficData dummy = new TrafficData();
        Page<TrafficData> page = new PageImpl<>(List.of(dummy));
        when(trafficDataService.getTrafficData(0, 10)).thenReturn(page);

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl("?page=0&size=10"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetTrafficDataById_found() {
        TrafficData dummy = new TrafficData();
        dummy.setId(1L);
        when(trafficDataService.getTrafficDataById(1L)).thenReturn(Optional.of(dummy));

        ResponseEntity<TrafficData> response = restTemplate.getForEntity(baseUrl("/1"), TrafficData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetTrafficDataById_notFound() {
        when(trafficDataService.getTrafficDataById(999L)).thenReturn(Optional.empty());

        ResponseEntity<TrafficData> response = restTemplate.getForEntity(baseUrl("/999"), TrafficData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetTrafficDataByNode() {
        when(trafficDataService.getTrafficDataByNode(5)).thenReturn(List.of(new TrafficData()));

        ResponseEntity<TrafficData[]> response = restTemplate.getForEntity(baseUrl("/node/5"), TrafficData[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetTrafficDataInTimeRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(trafficDataService.getTrafficDataInTimeRange(start, end)).thenReturn(List.of(new TrafficData()));

        String url = baseUrl("/timerange?start=" + start + "&end=" + end);
        ResponseEntity<TrafficData[]> response = restTemplate.getForEntity(url, TrafficData[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetAvailableResourceTypes() {
        when(trafficDataService.getAvailableResourceTypes()).thenReturn(List.of("CPU", "Memory"));

        ResponseEntity<String[]> response = restTemplate.getForEntity(baseUrl("/resource-types"), String[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("CPU");
    }

    @Test
    void testGetLatestTrafficDataForNode_found() {
        TrafficData data = new TrafficData();
        when(trafficDataService.getMostRecentTrafficDataByNode(2)).thenReturn(Optional.of(data));

        ResponseEntity<TrafficData> response = restTemplate.getForEntity(baseUrl("/latest-per-node/2"), TrafficData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetLatestTrafficDataForNode_notFound() {
        when(trafficDataService.getMostRecentTrafficDataByNode(99)).thenReturn(Optional.empty());

        ResponseEntity<TrafficData> response = restTemplate.getForEntity(baseUrl("/latest-per-node/99"), TrafficData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
