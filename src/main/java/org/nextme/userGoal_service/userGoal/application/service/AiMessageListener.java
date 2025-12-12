package org.nextme.userGoal_service.userGoal.application.service;

import org.nextme.userGoal_service.userGoal.domain.entity.AiMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AiMessageListener {
    @KafkaListener(topics = "ai.message", containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageTpl message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        AiMessage om = (AiMessage)message;
        System.out.printf("수신 받은 메세지 key=%s, message=%s%n", key, om.getOrderId().toString());
    }
}
