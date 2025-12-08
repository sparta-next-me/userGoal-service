package org.nextme.userGoal_service.userGoal.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.nextme.common.jpa.BaseEntity;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.UserGoalRequest;

import java.util.UUID;

@Table(name = "p_userGoal")
@Getter
@ToString
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGoal extends BaseEntity {
    @EmbeddedId
    // 목표 아이디
    private UserGoalId id;

    @Column(name = "age")
    // 사용자 나이
    private String age;

    @Column(name = "job")
    // 사용자 직업
    private String job;

    @Column(name = "capital")
    @ColumnDefault("0")
    // 자본금
    private int capital;

    @Column(name = "monthly_income")
    @ColumnDefault("0")
    //월수입
    private int monthlyIncome;

    @Column(name = "goal_detail")
    // 목표상세
    private String goalDetail;

    @Column(name = "fixed_expenses")
    @ColumnDefault("0")
    //월고정지출
    private int fixedExpenses;

    @Column(name = "user_id", nullable = false)
    // 사용자 ID
    private UUID userId;


}
