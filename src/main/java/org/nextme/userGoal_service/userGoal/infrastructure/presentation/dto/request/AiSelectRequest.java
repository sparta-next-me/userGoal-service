package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request;

import java.util.UUID;

public record AiSelectRequest(
        UUID reportId // 분석Id

) {
}
