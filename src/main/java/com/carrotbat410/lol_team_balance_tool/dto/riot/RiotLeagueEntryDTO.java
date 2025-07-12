package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RiotLeagueEntryDTO {
    private String leagueId;
    private String summonerId;
    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;

    @Override
    public String toString() {
        return "RiotLeagueEntryDTO{" +
                "leagueId='" + leagueId + '\'' +
                ", summonerId='" + summonerId + '\'' +
                ", queueType='" + queueType + '\'' +
                ", tier='" + tier + '\'' +
                ", rank='" + rank + '\'' +
                ", leaguePoints=" + leaguePoints +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}
