package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RiotSummonerDTO {
    private final String id;
    private final String accountId;
    private final String puuid;
    private final long revisionDate;
    private final int profileIconId;
    private final long summonerLevel;

    @Override
    public String toString() {
        return "RiotSummonerDTO{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", revisionDate=" + revisionDate +
                ", profileIconId=" + profileIconId +
                ", summonerLevel=" + summonerLevel +
                '}';
    }
}
