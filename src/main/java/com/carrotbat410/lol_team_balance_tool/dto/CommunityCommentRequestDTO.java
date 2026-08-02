package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityCommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 1000, message = "댓글은 1000자 이하로 입력해주세요.")
    private String content;
}
