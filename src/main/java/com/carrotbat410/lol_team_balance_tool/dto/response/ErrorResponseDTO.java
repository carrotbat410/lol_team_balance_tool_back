package com.carrotbat410.lol_team_balance_tool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {
    private final String code;
    private final String message;

    public ErrorResponseDTO(String message) {
        this(null, message);
    }
}
