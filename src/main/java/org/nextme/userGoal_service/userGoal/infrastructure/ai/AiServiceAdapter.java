package org.nextme.userGoal_service.userGoal.infrastructure.ai;

import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;

public interface AiServiceAdapter {
    String answer(EmbeddingGoalRequest request);
    String chatAnswer(); // 챗봇 질문에 대한 응답값
}
