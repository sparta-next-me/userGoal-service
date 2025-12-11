package org.nextme.userGoal_service.userGoal.infrastructure.gemini;

import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;

public interface GeminiServiceAdapter {
    String answer(EmbeddingGoalRequest request);
}
