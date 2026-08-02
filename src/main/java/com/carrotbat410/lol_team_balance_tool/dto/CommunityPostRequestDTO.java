package com.carrotbat410.lol_team_balance_tool.dto;

import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityPostRequestDTO {

    @NotNull(message = "카테고리를 선택해주세요.")
    private CommunityPostCategory category;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 160, message = "제목은 160자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
