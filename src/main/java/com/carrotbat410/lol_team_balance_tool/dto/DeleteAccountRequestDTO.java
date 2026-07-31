package com.carrotbat410.lol_team_balance_tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원 탈퇴 요청 DTO")
public class DeleteAccountRequestDTO {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", example = "my_password")
    private String password;
}
