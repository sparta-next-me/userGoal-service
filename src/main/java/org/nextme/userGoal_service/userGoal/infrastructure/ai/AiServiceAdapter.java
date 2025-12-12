package org.nextme.userGoal_service.userGoal.infrastructure.ai;


import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;

public interface AiServiceAdapter {
    String answer(EmbeddingGoalRequest request);
    AiMessageResponse chatAnswer(ChatMessage chatMessage); // 챗봇 질문에 대한 응답값
}
