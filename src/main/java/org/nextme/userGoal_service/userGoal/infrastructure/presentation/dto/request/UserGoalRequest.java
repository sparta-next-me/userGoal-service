package org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request;

import java.util.UUID;

public record UserGoalRequest(
        String age, //나이
        String job, // 직업
        int capital, // 자본금
        int monthlyIncome, //월수입
        int fixedExpenses // 월 고정지출
) {
}
