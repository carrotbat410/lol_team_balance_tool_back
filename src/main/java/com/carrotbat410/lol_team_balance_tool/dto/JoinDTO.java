package com.carrotbat410.lol_team_balance_tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class JoinDTO {

    @Schema(description = "사용자 ID", example = "my_user_id")
    private final String userId;

    @Schema(description = "비밀번호", example = "my_password")
    private final String password;
}
