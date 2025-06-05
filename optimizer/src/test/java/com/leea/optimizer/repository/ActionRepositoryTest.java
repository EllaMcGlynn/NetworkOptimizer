//package com.leea.optimizer.repository;
//
//import com.leea.optimizer.entity.OptimizerActionEntity;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ActiveProfiles("test")  // Uses application-test.yml
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Don't replace with H2
//public class ActionRepositoryTest {
//
//    @Autowired
//    private ActionRepository repo;
//
//    @Test
//    public void testSaveAndRetrieveAction() {
//        OptimizerActionEntity action = new OptimizerActionEntity();
//        action.setNodeId(1);
//        action.setResourceType("CPU");
//        action.setAmount(15);
//        action.setActionType("INCREASE");
//        action.setExecutedBy("OPTIMIZER");
//        action.setTimestamp(LocalDateTime.now());
//
//        repo.save(action);
//
//        var results = repo.findAll();
//        assertNotNull(results);
//        assertEquals(1, results.size());
//
//        OptimizerActionEntity saved = results.get(0);
//        assertEquals("CPU", saved.getResourceType());
//        assertEquals("OPTIMIZER", saved.getExecutedBy());
//    }
//}
