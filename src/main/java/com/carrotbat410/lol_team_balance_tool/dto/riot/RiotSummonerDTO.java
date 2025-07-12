package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RiotSummonerDTO {
    private String id;
    private String accountId;
    private String puuid;
    private long revisionDate;
    private int profileIconId;
    private long summonerLevel;

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
