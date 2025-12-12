package org.nextme.userGoal_service.userGoal.application.service;

import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.ai.AiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AiMessageListener {
    private AiServiceAdapter aiServiceAdapter;


    @KafkaListener(topics = "chat.message",  groupId = "chat-group",containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageTpl message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        ChatMessage response = (ChatMessage) message;

        aiServiceAdapter.chatAnswer(response);


        System.out.printf("수신 받은 메세지 key=%s, message=%s%n", key, message);
    }
}
