package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response;

import lombok.Builder;
import org.nextme.userGoal_service.userGoal.domain.entity.Report;

@Builder
public record AiResponse(
        String question, //질문
        String resultReport // 분석결과
) {
    public static AiResponse of(Report report) {
        return AiResponse.builder()
                .question(report.getQuestion())
                .resultReport(report.getResultReport())
                .build();
    }
}
