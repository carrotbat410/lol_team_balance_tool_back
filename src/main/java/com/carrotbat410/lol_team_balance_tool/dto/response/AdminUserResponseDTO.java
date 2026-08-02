package com.carrotbat410.lol_team_balance_tool.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminUserResponseDTO {

    private final int no;
    private final String userId;
    private final String role;
    private final LocalDateTime createdAt;

    public AdminUserResponseDTO(int no, String userId, String role, LocalDateTime createdAt) {
        this.no = no;
        this.userId = userId;
        this.role = role;
        this.createdAt = createdAt;
    }
}
