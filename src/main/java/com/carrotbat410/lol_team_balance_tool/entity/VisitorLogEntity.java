package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "visitor_logs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_logs_date_key", columnNames = {"visit_date", "visitor_key"})
        },
        indexes = {
                @Index(name = "idx_visitor_logs_visit_date", columnList = "visit_date"),
                @Index(name = "idx_visitor_logs_user_id", columnList = "user_id"),
                @Index(name = "idx_visitor_logs_visitor_key", columnList = "visitor_key")
        }
)
public class VisitorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visitor_key", nullable = false, length = 160)
    private String visitorKey;

    @Column(name = "visitor_id", length = 80)
    private String visitorId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "first_path", length = 500)
    private String firstPath;

    @Column(name = "first_visited_at", nullable = false)
    private LocalDateTime firstVisitedAt;

    @Column(name = "last_visited_at", nullable = false)
    private LocalDateTime lastVisitedAt;

    @Column(name = "hit_count", nullable = false)
    private int hitCount;
}
