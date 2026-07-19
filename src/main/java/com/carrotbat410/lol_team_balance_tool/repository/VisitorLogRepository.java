package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.VisitorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorLogRepository extends JpaRepository<VisitorLogEntity, Long> {

    Optional<VisitorLogEntity> findByVisitDateAndVisitorKey(LocalDate visitDate, String visitorKey);

    List<VisitorLogEntity> findByVisitDateBetweenOrderByVisitDateAsc(LocalDate startDate, LocalDate endDate);
}
