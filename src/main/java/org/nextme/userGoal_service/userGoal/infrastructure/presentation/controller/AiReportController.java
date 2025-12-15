package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;


import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.AiReportService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.AiSelectRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/usergoal/report")
@RequiredArgsConstructor
public class AiReportController {
    private final AiReportService aiReportService;

    @PostMapping("/create")
    // 사용자 목표 분석
    public ResponseEntity<CustomResponse<AiResponse>> create(@RequestBody EmbeddingGoalRequest request) {
        AiResponse response = aiReportService.create(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 생성되었습니다.", response));
    }

    @PostMapping("/all")
    //분석 전체조회
    public ResponseEntity<CustomResponse<List<AiResponse>>> getAll(@RequestBody AiSelectRequest request) {
        List<AiResponse> response = aiReportService.getAll(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @PostMapping
    //분석 단건조회
    public ResponseEntity<CustomResponse<AiResponse>> getOne(@RequestBody AiSelectRequest request) {
        AiResponse response = aiReportService.getOne(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @DeleteMapping
    // 분석삭제
    public ResponseEntity<CustomResponse> delete(@RequestParam UUID reportId) {
        aiReportService.deleteReport(reportId);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석결과가 삭제되었습니다."));
    }
}


