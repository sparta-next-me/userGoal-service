package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request;

import java.util.UUID;

public record EmbeddingGoalRequest(
        UUID userid, // 사용자 아이디
        String goalDetail // 사용자 목표 상세
) {
}
