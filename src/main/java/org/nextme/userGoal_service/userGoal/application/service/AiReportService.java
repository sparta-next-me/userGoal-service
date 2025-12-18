package org.nextme.userGoal_service.userGoal.application.service;


import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.Report;
import org.nextme.userGoal_service.userGoal.domain.entity.ReportId;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.ReportRepository;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.ai.AiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.AiSelectRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiReportService {
    private final AiServiceAdapter aiServiceAdapter;
    private final UserGoalRepository userGoalRepository;
    private final ReportRepository reportRepository;

    // 추천 금융상품 반환하기 위한 로직
    public AiResponse create(EmbeddingGoalRequest request,  UUID userId) {
        // 사용자 맞는지
        UserGoal userGoal = userGoalRepository.findByUserId(userId);

        // 조회한 사용자 아이디가 null이라면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        // gemini 호출
        String recommendation = aiServiceAdapter.answer(request, userId);
        String cleanOutput = recommendation.replaceAll("(?is)<thought>.*?</thought>", "");

        Report report = Report.builder()
                .id(ReportId.of(UUID.randomUUID()))
                .question(request.question())
                .userGoal(userGoal)
                .modelName("gpt-5-nano")
                .resultReport(cleanOutput)
                .build();

        reportRepository.save(report);

        return new AiResponse(report.getQuestion(),cleanOutput);
    }


    // 전체조회
    public List<AiResponse> getAll(AiSelectRequest request,UUID userId) {

        // 조회할 사용자가 있는지
        List<Report> reportResponse = reportRepository.findByUserGoalUserIdOrderByCreatedAtDesc(userId);

        return reportResponse.stream().map(AiResponse::of).collect(Collectors.toList());

    }

    // 단건조회
    public AiResponse getOne(AiSelectRequest request,UUID userId) {
        Report reportResponse = reportRepository.findByUserGoalUserIdAndId(userId, ReportId.of(request.reportId()));
        if (reportResponse == null) {
            throw new GoalException(GoalErrorCode.REPORT_NOT_FOUND);
        }
        return AiResponse.of(reportResponse);
    }

    public void deleteReport(UUID reportId) {
        // 삭제할 사용자가 있는지
        Report report = reportRepository.findById(ReportId.of(reportId));
        if (report == null) {
            throw new GoalException(GoalErrorCode.REPORT_ID_NOT_FOUND);
        }

        reportRepository.delete(report);
    }
}
