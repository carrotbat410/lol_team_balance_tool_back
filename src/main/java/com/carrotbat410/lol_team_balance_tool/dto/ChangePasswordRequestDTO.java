package com.carrotbat410.lol_team_balance_tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class ChangePasswordRequestDTO {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", example = "current_password")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Schema(description = "새 비밀번호", example = "new_password", minLength = 6, maxLength = 72)
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    @Schema(description = "새 비밀번호 확인", example = "new_password", minLength = 6, maxLength = 72)
    private String newPasswordConfirm;
}
