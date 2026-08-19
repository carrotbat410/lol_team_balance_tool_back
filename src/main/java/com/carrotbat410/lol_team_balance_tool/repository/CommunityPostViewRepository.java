package com.carrotbat410.lol_team_balance_tool.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class CommunityPostViewRepository {

    private final JdbcTemplate jdbcTemplate;

    public int insertIgnore(Long postNo, String viewerKeyHash, LocalDate viewDate, LocalDateTime createdAt) {
        return jdbcTemplate.update(
                """
                INSERT IGNORE INTO community_post_views (post_no, viewer_key_hash, view_date, created_at)
                VALUES (?, ?, ?, ?)
                """,
                postNo,
                viewerKeyHash,
                viewDate,
                createdAt
        );
    }
}
