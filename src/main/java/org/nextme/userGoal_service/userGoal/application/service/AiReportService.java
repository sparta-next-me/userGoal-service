package org.nextme.userGoal_service.userGoal.application.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.Report;
import org.nextme.userGoal_service.userGoal.domain.entity.ReportId;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoalId;
import org.nextme.userGoal_service.userGoal.domain.repository.ReportRepository;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.ai.AiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.AiSelectRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiSelectResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiReportService {
    private final AiServiceAdapter aiServiceAdapter;
    private final UserGoalRepository userGoalRepository;
    private final ReportRepository reportRepository;

    // 추천 금융상품 반환하기 위한 로직
    public AiResponse create(EmbeddingGoalRequest request,  UUID userId) {

        log.info("userId 확인 : " + userId);
        // 사용자 맞는지
        UserGoal userGoal = userGoalRepository.findByUserId(userId);
        log.info("유저 조회 확인 : " + userGoal.toString());

        // 조회한 사용자 아이디가 null이라면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        // gemini 호출
        String recommendation = aiServiceAdapter.answer(request, userId);
        log.info("추천 결과 확인 : " + recommendation);
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
    public List<AiSelectResponse> getAll(UUID userId) {

        // 조회할 사용자가 있는지
        List<Report> reportResponse = reportRepository.findByUserGoalUserIdOrderByCreatedAtDesc(userId);

        System.out.println(reportResponse.toString() + " 전체");

        return reportResponse.stream().map(AiSelectResponse::of).collect(Collectors.toList());

    }

    // 단건조회
    public AiSelectResponse getOne(AiSelectRequest request) {
        Report reportResponse = reportRepository.findById(ReportId.of(request.reportId()));
        if (reportResponse == null) {
            throw new GoalException(GoalErrorCode.REPORT_NOT_FOUND);
        }
        return AiSelectResponse.of(reportResponse);
    }

    public void deleteReport(UUID reportId) {
        // 삭제할 분석결과가 있는지
        Report report = reportRepository.findById(ReportId.of(reportId));
        if (report == null) {
            throw new GoalException(GoalErrorCode.REPORT_ID_NOT_FOUND);
        }

        reportRepository.delete(report);
    }

}
