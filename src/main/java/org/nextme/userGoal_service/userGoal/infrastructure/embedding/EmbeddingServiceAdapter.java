package org.nextme.userGoal_service.userGoal.infrastructure.embedding;

import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq.UpdateUserGoalEvent;

public interface EmbeddingServiceAdapter {
    void embeddingGoal(EmbeddingGoalRequest request); // 생성용
    void updateEmbeddingGoal(UpdateUserGoalEvent updateUserGoalEvent); // 수정용
}
