package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;


import lombok.RequiredArgsConstructor;
import org.nextme.common.security.UserPrincipal;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.AiReportService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.AiSelectRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/usergoal/report")
@RequiredArgsConstructor
public class AiReportController {
    private final AiReportService aiReportService;

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PostMapping("/create")
    // 사용자 목표 분석
    public ResponseEntity<CustomResponse<AiResponse>> create(@RequestBody EmbeddingGoalRequest request,@AuthenticationPrincipal UserPrincipal principal) {
        AiResponse response = aiReportService.create(request,UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 생성되었습니다.", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PostMapping("/all")
    //분석 전체조회
    public ResponseEntity<CustomResponse<List<AiResponse>>> getAll(@RequestBody AiSelectRequest request,@AuthenticationPrincipal UserPrincipal principal) {
        List<AiResponse> response = aiReportService.getAll(request,UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PostMapping
    //분석 단건조회
    public ResponseEntity<CustomResponse<AiResponse>> getOne(@RequestBody AiSelectRequest request,@AuthenticationPrincipal UserPrincipal principal) {
        AiResponse response = aiReportService.getOne(request,UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @DeleteMapping
    // 분석삭제
    public ResponseEntity<CustomResponse> delete(@RequestParam UUID reportId) {
        aiReportService.deleteReport(reportId);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석결과가 삭제되었습니다."));
    }
}


