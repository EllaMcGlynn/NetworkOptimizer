//package com.leea.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.any;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.leea.SubscriberApplication;
//import com.leea.models.TrafficData;
//import com.leea.repo.DataRepo;
//
//@SpringBootTest(classes = SubscriberApplication.class)
//@AutoConfigureMockMvc
//class KafkaConsumerServiceTest {
//
//    @Autowired
//    private KafkaConsumerService kafkaConsumerService;
//
//    @MockBean
//    private DataRepo repository;
//
//    @Test
//    public void testKafkaMessageProcessing() throws JsonProcessingException {
//        String jsonMessage = """
//            {
//                "nodeId": 1,
//                "networkId": 1,
//                "resourceUsage": { "cpu": 50.0, "memory": 50.5 , "bandwidth": 80.5 },
//	            "resourceAllocated": { "cpu": 70.0, "memory": 30.5 , "bandwidth": 100.5},
//	            "timestamp": 1748615400
//            }
//        """;
//        kafkaConsumerService.listen(jsonMessage);
//
//        ArgumentCaptor<TrafficData> captor = ArgumentCaptor.forClass(TrafficData.class);
//        verify(repository, times(1)).save(captor.capture());
//
//        TrafficData saved = captor.getValue();
//        assertEquals(1, saved.getNodeId());
//        assertEquals(1, saved.getNetworkId());
//        assertEquals(50.0, saved.getResourceUsage().get("cpu"));
//        assertEquals(50.5, saved.getResourceUsage().get("memory"));
//        assertEquals(70.0, saved.getResourceAllocated().get("cpu"));
//        assertEquals(30.5, saved.getResourceAllocated().get("memory"));
//    }
//
//    @Test
//    public void testErrorHandling() throws JsonProcessingException{
//        String jsonMessage1 = """
//                {
//                    "nodeId": 1,
//                    "networkId": 1,
//                    "resourceUsage": null,
//                    "resourceAllocated": { "cpu": 70.0, "memory": 30.5 , "bandwidth": 100.5},
//                    "timestamp": 1748615400
//                }
//            """;
//        kafkaConsumerService.listen(jsonMessage1);
//
//        String jsonMessage2 = """
//                {
//                    "nodeId": 1,
//                    "networkId": 1,
//                    "resourceUsage": { "cpu": 50.0, "memory": 50.5 , "bandwidth": 80.5 },
//                    "resourceAllocated": null,
//                    "timestamp": 1748615400
//                }
//            """;
//        kafkaConsumerService.listen(jsonMessage2);
//
//        verify(repository, times(0)).save(any());
//    }
//
//}

package com.leea.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.logger.MessageLogger;
import com.leea.models.TrafficData;
import com.leea.repo.DataRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private DataRepo repository;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Ensures LocalDateTime deserialization works
    }

    @Test
    void testValidMessage_savesDataAndLogs() throws JsonProcessingException {
        String jsonMessage = """
            {
              "id": 0,
              "nodeId": 1,
              "networkId": 2,
              "resourceUsage": {
                "CPU": 75.5,
                "RAM": 60.2
              },
              "resourceAllocated": {
                "CPU": 100.0,
                "RAM": 100.0
              },
              "timestamp": 1717675200.0
            }
        """;

        try (MockedStatic<MessageLogger> loggerMock = mockStatic(MessageLogger.class)) {
            kafkaConsumerService.listen(jsonMessage);

            verify(repository).save(any(TrafficData.class));
            loggerMock.verify(() -> MessageLogger.log(any(TrafficData.class)));
        }
    }

    @Test
    void testMissingTimestamp_logsError() throws JsonProcessingException {
        String jsonMissingTimestamp = """
        {
          "nodeId": 1,
          "networkId": 2,
          "resourceUsage": { "CPU": 75.5 },
          "resourceAllocated": { "CPU": 100.0 }
        }
    """;

        try (MockedStatic<MessageLogger> loggerMock = mockStatic(MessageLogger.class)) {
            kafkaConsumerService.listen(jsonMissingTimestamp);

            verify(repository, never()).save(any());
            loggerMock.verify(() -> MessageLogger.logError(contains("null value found entry")));
        }
    }


}

