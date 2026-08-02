package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserRoleUpdateRequestDTO {

    @NotBlank(message = "권한을 선택해주세요.")
    private String role;
}
