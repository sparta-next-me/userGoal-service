package org.nextme.userGoal_service.userGoal.infrastructure.embedding;

import org.nextme.userGoal_service.userGoal.application.dto.UpdateUserGoal;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;

public interface EmbeddingServiceAdapter {
    void embeddingGoal(EmbeddingGoalRequest request); // 생성용
    void updateEmbeddingGoal(UpdateUserGoal updateUserGoal); // 사용자 목표 수정용
}
