package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TeamBalanceResponseDTO {

    private final List<SummonerDTO> finalTeam1List;
    private final List<SummonerDTO> finalTeam2List;
    private final int finalTeam1AvgMmr;
    private final int finalTeam2AvgMmr;
    private final Tier finalTeam1AvgTier;
    private final int finalTeam1AvgRank;
    private final Tier finalTeam2AvgTier;
    private final int finalTeam2AvgRank;
    private final int avgMmrDiff;


}
