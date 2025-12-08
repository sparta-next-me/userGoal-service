package org.nextme.userGoal_service.userGoal.infrastructure.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.global.infrastructure.success.CustomResponse;
import org.nextme.userGoal_service.userGoal.application.service.UserGoalService;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
