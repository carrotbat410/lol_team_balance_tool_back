package com.carrotbat410.lol_team_balance_tool.dto.response;

import java.time.LocalDate;

public record VisitTrackResponseDTO(
        LocalDate visitDate,
        boolean countedToday
) {
}
