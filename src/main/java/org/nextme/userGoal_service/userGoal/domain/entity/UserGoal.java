package org.nextme.userGoal_service.userGoal.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.nextme.common.jpa.BaseEntity;

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
    private int capital;

    @Column(name = "monthly_income")
    private int monthlyIncome;

    @Column(name = "goal_detail")
    private String goalDetail;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
