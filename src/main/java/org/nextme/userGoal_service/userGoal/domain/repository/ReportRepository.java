package org.nextme.userGoal_service.userGoal.domain.repository;

import org.nextme.userGoal_service.userGoal.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
}
