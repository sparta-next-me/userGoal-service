package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response;

import lombok.Builder;
import org.nextme.userGoal_service.userGoal.domain.entity.Report;

import java.util.UUID;

@Builder
public record AiSelectResponse(
        UUID reportId,
        String question, //질문
        String resultReport,// 분석결과
        UUID goalId
) {
    public static AiSelectResponse of(Report report) {
        return AiSelectResponse.builder()
                .reportId(report.getId().getId())
                .question(report.getQuestion())
                .resultReport(report.getResultReport())
                .goalId(report.getUserGoal().getId().getId())
                .build();
    }
}