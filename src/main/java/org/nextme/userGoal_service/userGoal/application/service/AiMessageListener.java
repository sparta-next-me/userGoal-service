package org.nextme.userGoal_service.userGoal.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.ai.AiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiMessageListener {
    private final AiResultProducer aiResultProducer;


    @KafkaListener(topics = "chat.message",  groupId = "chat-group",containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageTpl message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        ChatMessage chatMessage = (ChatMessage) message;
        log.info("수신 받은 메세지 key={}, roomId={}", key, chatMessage.getRoomId());

        // AI 응답 생성 및 Kafka로 발행
        AiMessageResponse response = aiResultProducer.send(chatMessage);

        log.info("AI 응답 전송 완료 - roomId={}", response.getRoomId());
    }
}
