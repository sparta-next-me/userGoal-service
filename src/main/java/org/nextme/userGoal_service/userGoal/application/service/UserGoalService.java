package org.nextme.userGoal_service.userGoal.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoalId;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;

    // 사용자 목표 생성
    public void create(UserGoalRequest userGoalRequest) {

        // 목표 설계를 위한 정보를 전부 입력하지 않았다면
        if(userGoalRequest.age() == null && userGoalRequest.job() == null && userGoalRequest.monthlyIncome() == 0 &&
        userGoalRequest.capital() == 0 && userGoalRequest.fixedExpenses() == 0){
            throw new GoalException(GoalErrorCode.GOAL_MISSING_PARAMETER);
        }

        UserGoal userGoal = UserGoal.builder()
                .id(UserGoalId.of(UUID.randomUUID()))
                .age(userGoalRequest.age())
                .job(userGoalRequest.job())
                .capital(userGoalRequest.capital())
                .monthlyIncome(userGoalRequest.capital())
                .fixedExpenses(userGoalRequest.capital())
                .userId(UUID.randomUUID())
                .build();

        userGoalRepository.save(userGoal);
    }

}
