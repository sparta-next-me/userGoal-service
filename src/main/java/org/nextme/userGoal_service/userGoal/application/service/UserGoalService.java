package org.nextme.userGoal_service.userGoal.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoalId;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.UserGoalResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq.UpdatePublisher;
import org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq.UpdateUserGoalEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final UpdatePublisher updatePublisher;


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

    // 사용자 목표 수정
    public void update(UserGoalRequest userGoalRequest) {

        // 사용자가 작성한 목표가 있는지 확인
        UserGoal goal_user = userGoalRepository.findByUserId(userGoalRequest.userId());

        // 사용자가 작성한 목표가 없다면
        if(goal_user == null){
            throw new GoalException(GoalErrorCode.GOAL_NOT_FOUND);
        }

        // 수정할 정보가 있는지 획인 후 있다면 상태 업데이트
        boolean updatedGoal = goal_user.updateGoal(userGoalRequest);

        // 수정할 정보 없이 수정 요청을 했다면
        if(!updatedGoal){
            throw new GoalException(GoalErrorCode.GOAL_NOTING_CHANGE);
        }

        // 수정된 정보만 이벤트로 넘기기
        String updateData = "";

        updateData += "나이 : " + userGoalRequest.age();
        updateData +=" 직업 : " + userGoalRequest.job();
        updateData +=  " 자본금: " + userGoalRequest.capital();
        updateData += " 월수입 : " +  userGoalRequest.monthlyIncome();
        updateData += " 월 고정지출 : " +userGoalRequest.fixedExpenses();

        // 수정 완료 후 이벤트 발행
        UpdateUserGoalEvent event = new UpdateUserGoalEvent(
                userGoalRequest.userId(),
                updateData // 수정된 목표 상세만 보내기
        );

        // 이벤트 보내기
        updatePublisher.sendUpdateEvent(event);

    }

    // 사용자 목표 조회
    public UserGoalResponse getGoal(UUID userGoalId) {

        // 조회할 사용자가 있는지
        UserGoal userGoal = userGoalRepository.findByUserId(userGoalId);


        // 사용자 목표가 없다면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.GOAL_NOT_FOUND);
        }

        return UserGoalResponse.of(userGoal);
    }

    // 사용자 목표 삭제
    public void deleteGoal(UUID userGoalId) {
        // 삭제할 사용자가 있는지
        UserGoal userGoal = userGoalRepository.findByUserId(userGoalId);

        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.GOAL_NOT_FOUND);
        }

        userGoalRepository.delete(userGoal);

    }
}
