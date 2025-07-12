package com.carrotbat410.lol_team_balance_tool.dto.riot;

import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RiotLeagueEntryDTO {
    private final String leagueId;
    private final String summonerId;
    private final String queueType;
    private final Tier tier;
    private final String rank;
    private final int leaguePoints;
    private final int wins;
    private final int losses;

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
