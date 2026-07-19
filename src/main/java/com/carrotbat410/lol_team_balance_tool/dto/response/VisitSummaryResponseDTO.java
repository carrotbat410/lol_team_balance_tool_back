package com.carrotbat410.lol_team_balance_tool.dto.response;

import java.time.LocalDate;
import java.util.List;

public record VisitSummaryResponseDTO(
        int days,
        LocalDate startDate,
        LocalDate endDate,
        long totalUniqueVisitors,
        long loggedInUniqueVisitors,
        long guestUniqueVisitors,
        long totalDailyVisitors,
        List<DailyVisitStatsDTO> dailyStats,
        List<LoggedInVisitorStatsDTO> loggedInVisitors
) {
}
