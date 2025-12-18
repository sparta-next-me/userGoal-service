package org.nextme.userGoal_service.userGoal.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.dto.UpdateUserGoal;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoalId;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.embedding.EmbeddingServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.UserGoalResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final EmbeddingServiceAdapter embeddingServiceAdapter;



    // 사용자 목표 생성
    public void create(UserGoalRequest userGoalRequest, UUID userId) {

        // 목표 설계를 위한 정보를 전부 입력하지 않았다면
        if(userGoalRequest.age() == null && userGoalRequest.job() == null && userGoalRequest.monthlyIncome() == 0 &&
                userGoalRequest.capital() == 0 && userGoalRequest.fixedExpenses() == 0){
            throw new GoalException(GoalErrorCode.GOAL_MISSING_PARAMETER);
        }

        // 유저가 있는지 확인
        UserGoal userGoal_id = userGoalRepository.findByUserId(userId);

        if(userGoal_id!= null){
            throw new GoalException(GoalErrorCode.USER_ID_ALREADY_EXISTS);
        }

        UserGoal userGoal = UserGoal.builder()
                .id(UserGoalId.of(UUID.randomUUID()))
                .age(userGoalRequest.age())
                .job(userGoalRequest.job())
                .capital(userGoalRequest.capital())
                .monthlyIncome(userGoalRequest.monthlyIncome())
                .fixedExpenses(userGoalRequest.fixedExpenses())
                .userId(userId)
                .build();

        userGoalRepository.save(userGoal);
    }

    // 사용자 목표 수정
    public void update(UserGoalRequest userGoalRequest, UUID userId) {

        // 사용자가 작성한 목표가 있는지 확인
        UserGoal goal_user = userGoalRepository.findByUserId(userId);

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

        // 유저 아이디
        UUID updateUserId= userId;
        // 수정된 정보만 이벤트로 넘기기
        String updateData = "";

        updateData += "나이 : " + userGoalRequest.age();
        updateData +=" 직업 : " + userGoalRequest.job();
        updateData +=  " 자본금: " + userGoalRequest.capital();
        updateData += " 월수입 : " +  userGoalRequest.monthlyIncome();
        updateData += " 월 고정지출 : " +userGoalRequest.fixedExpenses();

        // 수정 완료 후 이벤트 발행
        UpdateUserGoal event = new UpdateUserGoal(
                updateUserId,
                updateData // 수정된 목표 상세만 보내기
        );

        // vector_store update 메소드 호출
        embeddingServiceAdapter.updateEmbeddingGoal(event);

    }

    // 사용자 목표 조회
    public UserGoalResponse getGoal(UUID userId) {

        // 조회할 사용자가 있는지
        UserGoal userGoal = userGoalRepository.findByUserId(userId);


        // 사용자 목표가 없다면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.GOAL_NOT_FOUND);
        }

        return UserGoalResponse.of(userGoal);
    }

    // 사용자 목표 삭제
    public void deleteGoal(UUID userId) {
        // 삭제할 사용자가 있는지
        UserGoal userGoal = userGoalRepository.findByUserId(userId);

        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.GOAL_NOT_FOUND);
        }

        userGoalRepository.delete(userGoal);

    }
}