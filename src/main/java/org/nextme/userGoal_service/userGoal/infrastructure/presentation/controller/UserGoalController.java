package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.UserGoalService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.UserGoalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/user-goal")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;

    @PostMapping
    // 사용자 목표 정보 생성
    public ResponseEntity<CustomResponse> create(@RequestBody UserGoalRequest request) {

        userGoalService.create(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 생성되었습니다."));

    }

    @PatchMapping
    // 사용자 목표 정보 수정
    public ResponseEntity<CustomResponse> update(@RequestBody UserGoalRequest request) {
        userGoalService.update(request);
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 수정되었습니다."));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<UserGoalResponse>> getGoal(@RequestParam UUID userGoalId) {
        UserGoalResponse response = userGoalService.getGoal(userGoalId);
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 조회되었습니다.",response));

    }

    @DeleteMapping
    public ResponseEntity<CustomResponse> delete(@RequestParam UUID userGoalId) {
        userGoalService.deleteGoal(userGoalId);
        return ResponseEntity.ok(CustomResponse.onSuccess("목표가 삭제되었습니다."));
    }
}
