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

    @Column(name = "fixed_expenses")
    @ColumnDefault("0")
    //월고정지출
    private int fixedExpenses;

    @Column(name = "user_id", nullable = false)
    // 사용자 ID
    private UUID userId;

    public boolean updateGoal(UserGoalRequest userGoalRequest) {
        boolean updatedGoal = false;

        // 나이를 수정한다면
        if(userGoalRequest.age() != null && !userGoalRequest.age().equals(this.age)){
            this.age = userGoalRequest.age();
            updatedGoal = true;
        }

        // 직업을 수정한다면
        if(userGoalRequest.job() != null && !userGoalRequest.job().equals(this.job)) {
            this.job = userGoalRequest.job();
            updatedGoal = true;
        }

        // 자본금을 수정한다면
        if(userGoalRequest.capital() != 0 && userGoalRequest.capital() != this.capital) {
            this.capital = userGoalRequest.capital();
            updatedGoal = true;
        }

        // 월수입을 수정한다면
        if(userGoalRequest.monthlyIncome() != 0 && userGoalRequest.monthlyIncome() != this.monthlyIncome) {
            this.monthlyIncome = userGoalRequest.monthlyIncome();
            updatedGoal = true;
        }

        // 월고정지출을 수정한다면
        if(userGoalRequest.fixedExpenses() != 0 && userGoalRequest.fixedExpenses() != this.fixedExpenses) {
            this.fixedExpenses = userGoalRequest.fixedExpenses();
            updatedGoal = true;
        }
        return updatedGoal;

    }

//
//    // 사용자 목표 추가
//    public void updateUserGoal(String goalDetail) {
//        this.goalDetail = goalDetail;
//    }
}
