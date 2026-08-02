package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySettingUpdateRequestDTO {

    @NotNull(message = "커뮤니티 공개 여부를 선택해주세요.")
    private Boolean visibleToUsers;

    @NotNull(message = "공지사항 노출 개수를 입력해주세요.")
    @Min(value = 0, message = "공지사항 노출 개수는 0개 이상이어야 합니다.")
    @Max(value = 10, message = "공지사항 노출 개수는 10개 이하로 설정해주세요.")
    private Integer noticeDisplayCount;
}
