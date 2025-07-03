package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiotSummonerDTO {
    private String id;
    private String accountId;
    private String puuid;
    private long revisionDate;
    private int profileIconId;
    private long summonerLevel;
}
