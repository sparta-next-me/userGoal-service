package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request;

import java.util.UUID;

public record EmbeddingGoalRequest(
        UUID userid, // 사용자 아이디
        String question // 사용자 질문
) {
}
