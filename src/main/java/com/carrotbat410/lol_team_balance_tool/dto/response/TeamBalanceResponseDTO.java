package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class TeamBalanceResponseDTO {

    private final TeamBalanceTeamInfo team1Info;
    private final TeamBalanceTeamInfo team2Info;
    private final int avgMmrDiff;

    public TeamBalanceResponseDTO(TeamBalanceTeamInfo team1Info, TeamBalanceTeamInfo team2Info) {
        this.team1Info = team1Info;
        this.team2Info = team2Info;
        this.avgMmrDiff = Math.abs(team1Info.getFinalTeamAvgMmr() - team2Info.getFinalTeamAvgMmr());
    }
}

@Getter
@RequiredArgsConstructor
class TeamBalanceTeamInfo {
    private final List<SummonerDTO> finalTeamList;
    private final int finalTeamAvgMmr;
    private final Tier finalTeamAvgTier;
    private final int finalTeamAvgRank;
}

