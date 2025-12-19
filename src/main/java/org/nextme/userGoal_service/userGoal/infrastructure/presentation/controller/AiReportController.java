package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;


import lombok.RequiredArgsConstructor;
import org.nextme.common.security.UserPrincipal;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.AiReportService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.AiSelectRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiSelectResponse;
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
    @GetMapping("/all")
    //분석 전체조회
    public ResponseEntity<CustomResponse<List<AiSelectResponse>>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        List<AiSelectResponse> response = aiReportService.getAll(UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PostMapping
    //분석 단건조회
    public ResponseEntity<CustomResponse<AiSelectResponse>> getOne(@RequestBody AiSelectRequest request) {
        AiSelectResponse response = aiReportService.getOne(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석 결과가 전체조회 되었습니다.", response));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @DeleteMapping
    // 분석삭제(단건)
    public ResponseEntity<CustomResponse> delete(@RequestParam UUID reportId) {
        aiReportService.deleteReport(reportId);
        return ResponseEntity.ok(CustomResponse.onSuccess("분석결과가 삭제되었습니다."));
    }

}


