package com.carrotbat410.lol_team_balance_tool.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoggedInVisitorStatsDTO(
        String userId,
        long visitDays,
        long hitCount,
        LocalDate firstVisitDate,
        LocalDate lastVisitDate,
        LocalDateTime lastVisitedAt
) {
}
