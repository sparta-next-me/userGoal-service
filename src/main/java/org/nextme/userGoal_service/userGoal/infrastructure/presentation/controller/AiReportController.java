package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;


import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.AiReportService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/report")
@RequiredArgsConstructor
public class AiReportController {
    private final AiReportService aiReportService;

    @PostMapping("/create")
    // 사용자 목표 분석
    public ResponseEntity<CustomResponse<AiResponse>> create(@RequestBody EmbeddingGoalRequest request) {
        AiResponse response = aiReportService.create(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 생성되었습니다.", response));
    }
}


