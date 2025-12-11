package org.nextme.userGoal_service.userGoal.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.nextme.common.jpa.BaseEntity;

@Table(name = "p_report")
@Getter
@ToString
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @EmbeddedId
    // 분석 ID
    private ReportId id;

    @JoinColumn(name = "goal_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    // 목표 ID
    private UserGoal userGoal;

    @Column(name = "model_name", nullable = false)
    // 사용모델
    private String modelName;

    @Column(name ="result_report", nullable = false,columnDefinition = "TEXT")
    // 분석 결과
    private String resultReport;
}
