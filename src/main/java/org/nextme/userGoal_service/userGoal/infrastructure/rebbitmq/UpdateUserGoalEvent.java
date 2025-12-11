package org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateUserGoalEvent {
    private UUID userId; // 사용자 아이디
    private String updateGoal; // 수정된 목표 정보
}
