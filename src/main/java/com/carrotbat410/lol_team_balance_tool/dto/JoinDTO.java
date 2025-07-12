package com.carrotbat410.lol_team_balance_tool.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class JoinDTO {

    private final String userId;
    private final String password;
}
