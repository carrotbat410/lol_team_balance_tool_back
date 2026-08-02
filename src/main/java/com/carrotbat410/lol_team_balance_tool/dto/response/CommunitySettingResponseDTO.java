package com.carrotbat410.lol_team_balance_tool.dto.response;

import lombok.Getter;

@Getter
public class CommunitySettingResponseDTO {

    private final boolean visibleToUsers;
    private final int noticeDisplayCount;

    public CommunitySettingResponseDTO(boolean visibleToUsers, int noticeDisplayCount) {
        this.visibleToUsers = visibleToUsers;
        this.noticeDisplayCount = noticeDisplayCount;
    }
}
