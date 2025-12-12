package org.nextme.userGoal_service.userGoal.infrastructure.kafka.producer;


import org.nextme.userGoal_service.userGoal.application.service.AiResultProducer;

import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.ai.AiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.MessageTpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestProducer implements AiResultProducer {
    private KafkaTemplate<String, MessageTpl> kafkaTemplate;
    private AiServiceAdapter aiServiceAdapter;

    public TestProducer(KafkaTemplate<String, MessageTpl> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public AiMessageResponse send(ChatMessage chatMessage) {

        // LLM 결과 가져오기
        AiMessageResponse response = aiServiceAdapter.chatAnswer(chatMessage);

        // 챗봇에서 받은 정보 복사
        AiMessageResponse result_response = AiMessageResponse.builder()
                .roomId(chatMessage.getRoomId())
                .roomType(chatMessage.getRoomType())
                .content(response.getContent())   // LLM에서 생성한 content
                .build();

        // Kafka로 발행
        kafkaTemplate.send("ai.message", "aiMessage", result_response);

        return result_response;
    }
}
