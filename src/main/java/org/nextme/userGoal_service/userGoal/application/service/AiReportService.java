package org.nextme.userGoal_service.userGoal.application.service;


import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.Report;
import org.nextme.userGoal_service.userGoal.domain.entity.ReportId;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.ReportRepository;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.gemini.GeminiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiReportService {
    private final GeminiServiceAdapter geminiServiceAdapter;
    private final UserGoalRepository userGoalRepository;
    private final ReportRepository reportRepository;

    // 추천 금융상품 반환하기 위한 로직
    public AiResponse create(EmbeddingGoalRequest request) {
        // 사용자 맞는지
        UserGoal userGoal = userGoalRepository.findByUserId(request.userid());

        userGoal.updateUserGoal(request.goalDetail());

        // 조회한 사용자 아이디가 null이라면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        // gemini 호출
        String recommendation = geminiServiceAdapter.answer(request);
        String cleanOutput = recommendation.replaceAll("(?is)<thought>.*?</thought>", "");

        Report report = Report.builder()
                .id(ReportId.of(UUID.randomUUID()))
                .userGoal(userGoal)
                .modelName("Ollama")
                .resultReport(cleanOutput)
                .build();

        reportRepository.save(report);

        return new AiResponse(cleanOutput);
    }

}
