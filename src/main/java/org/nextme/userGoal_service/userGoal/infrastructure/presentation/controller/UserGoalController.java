package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.nextme.common.security.UserPrincipal;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.UserGoalService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.UserGoalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/usergoal")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PostMapping
    // 사용자 목표 정보 생성
    public ResponseEntity<CustomResponse> create(@RequestBody UserGoalRequest request, @AuthenticationPrincipal UserPrincipal principal) {

        userGoalService.create(request, UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 생성되었습니다."));

    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @PatchMapping
    // 사용자 목표 정보 수정
    public ResponseEntity<CustomResponse> update(@RequestBody UserGoalRequest request,@AuthenticationPrincipal UserPrincipal principal) {
        userGoalService.update(request,UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 수정되었습니다."));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADVISOR')")
    @GetMapping
    // 목표 조회
    public ResponseEntity<CustomResponse<UserGoalResponse>> getGoal(@AuthenticationPrincipal UserPrincipal principal) {
        UserGoalResponse response = userGoalService.getGoal(UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 조회되었습니다.",response));

    }

    @DeleteMapping
    // 목표삭제
    public ResponseEntity<CustomResponse> delete(@AuthenticationPrincipal UserPrincipal principal) {
        userGoalService.deleteGoal(UUID.fromString(principal.userId()));
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 삭제되었습니다."));
    }
}
