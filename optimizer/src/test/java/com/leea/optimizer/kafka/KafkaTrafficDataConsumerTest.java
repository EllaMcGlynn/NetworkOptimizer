//package com.leea.optimizer.kafka;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.leea.optimizer.OptimizerApplication;
//import com.leea.optimizer.service.KafkaTrafficDataConsumer;
//
//@SpringBootTest(classes = OptimizerApplication.class)
//@AutoConfigureMockMvc
//class KafkaTrafficDataConsumerTest {
//
//	@Autowired
//    private KafkaTrafficDataConsumer kafkaConsumerService;
//
//    @Test
//    public void testKafkaMessageProcessing() throws JsonProcessingException {
//    	String jsonMessage = """
//            {
//                "nodeId": 1,
//                "networkId": 1,
//                "resourceUsage": { "cpu": 50.0, "memory": 50.5 , "bandwidth": 80.5 },
//                "resourceAllocated": { "cpu": 70.0, "memory": 30.5 , "bandwidth": 100.5},
//                "timestamp": 1748615400.0
//            }
//        """;
//        try {
//			kafkaConsumerService.listen(jsonMessage);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//        assertEquals(1, kafkaConsumerService.getRecentData().size());
//    }
//
//    @Test
//    public void testErrorHandling() throws JsonProcessingException{
//    	int startValue = kafkaConsumerService.getRecentData().size();
//    	String jsonMessage1 = """
//                {
//                    "nodeId": 1,
//                    "networkId": 1,
//                    "resourceUsage": null,
//                    "resourceAllocated": { "cpu": 70.0, "memory": 30.5 , "bandwidth": 100.5},
//                    "timestamp": 1748615400.0
//                }
//            """;
//        try {
//			kafkaConsumerService.listen(jsonMessage1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//        String jsonMessage2 = """
//                {
//                    "nodeId": 1,
//                    "networkId": 1,
//                    "resourceUsage": { "cpu": 50.0, "memory": 50.5 , "bandwidth": 80.5 },
//                    "resourceAllocated": null,
//                    "timestamp": 1748615400.0
//                }
//            """;
//        try {
//			kafkaConsumerService.listen(jsonMessage2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//        assertEquals(startValue, kafkaConsumerService.getRecentData().size());
//    }
//}
