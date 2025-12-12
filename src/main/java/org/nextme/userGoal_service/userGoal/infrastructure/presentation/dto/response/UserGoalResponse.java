package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.response;

import lombok.Builder;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;

@Builder
public record UserGoalResponse(
        String age, //나이
        String job, // 직업
        int capital, // 자본금
        int monthlyIncome, //월수입
        int fixedExpenses // 월 고정지출

) {
    public static UserGoalResponse of(UserGoal userGoal) {
        return UserGoalResponse.builder()
                .age(userGoal.getAge())
                .job(userGoal.getJob())
                .capital(userGoal.getCapital())
                .monthlyIncome(userGoal.getMonthlyIncome())
                .fixedExpenses(userGoal.getFixedExpenses())
                .build();
    }
}
