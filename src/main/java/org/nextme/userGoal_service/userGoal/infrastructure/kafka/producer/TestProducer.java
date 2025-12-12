package org.nextme.userGoal_service.userGoal.infrastructure.kafka.producer;


import org.nextme.userGoal_service.userGoal.application.service.AiResultProducer;
import org.nextme.userGoal_service.userGoal.domain.entity.AiMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestProducer implements AiResultProducer {
    private KafkaTemplate<String, MessageTpl> kafkaTemplate;

    public TestProducer(KafkaTemplate<String, MessageTpl> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(UUID orderId) {
        kafkaTemplate.send("ai.message","orderNo",new AiMessage(UUID.randomUUID()));
    }


}
