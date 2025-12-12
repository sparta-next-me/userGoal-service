package org.nextme.userGoal_service;

import org.junit.jupiter.api.Test;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.producer.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class UserGoalServiceApplicationTests {

    @Autowired
    private TestProducer testProducer;
	@Test
	void contextLoads() {
        testProducer.send(UUID.randomUUID());

	}

}
