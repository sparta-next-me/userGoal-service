package org.nextme.userGoal_service.userGoal.application.service;


import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.gemini.GeminiServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response.AiResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiReportService {
    private final GeminiServiceAdapter geminiServiceAdapter;
    private final UserGoalRepository userGoalRepository;

    // 추천 금융상품 반환하기 위한 로직
    public AiResponse create(EmbeddingGoalRequest request) {
        // 사용자 맞는지
        UserGoal userGoal = userGoalRepository.findByUserId(request.userid());

        // 조회한 사용자 아이디가 null이라면
        if (userGoal == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        // 사용자 목표 상세 가져오기
        String goal_detail = request.goalDetail();

        // gemini 호출
        String recommendation = geminiServiceAdapter.answer(request);

        return new AiResponse(recommendation);
    }

}
