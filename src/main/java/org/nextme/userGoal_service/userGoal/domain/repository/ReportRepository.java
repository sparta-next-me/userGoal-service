package org.nextme.userGoal_service.userGoal.domain.repository;

import org.nextme.userGoal_service.userGoal.domain.entity.Report;
import org.nextme.userGoal_service.userGoal.domain.entity.ReportId;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByUserGoalUserIdOrderByCreatedAtDesc(UUID uuid);

    Report findByUserGoalUserIdAndId(UUID userId, ReportId reportId);

    Report findById(ReportId reportId);

    List<Report> findByIdAndUserGoalUserIdOrderByCreatedAtDesc(UUID reportId, UUID userId);
}
