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
    private ReportId id;

    @JoinColumn(name = "goal_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserGoal userGoal;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name ="result_report", nullable = false)
    private String resultReport;
}
