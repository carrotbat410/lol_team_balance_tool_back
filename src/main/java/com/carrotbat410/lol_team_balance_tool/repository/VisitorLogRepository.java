package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.VisitorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorLogRepository extends JpaRepository<VisitorLogEntity, Long> {

    Optional<VisitorLogEntity> findByVisitDateAndVisitorKey(LocalDate visitDate, String visitorKey);

    List<VisitorLogEntity> findByVisitDateBetweenOrderByVisitDateAsc(LocalDate startDate, LocalDate endDate);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE VisitorLogEntity v SET v.userId = null WHERE v.userId = :userId")
    int anonymizeUserId(@Param("userId") String userId);
}
