package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class TeamBalanceResponseDTO {

    private final List<SummonerDTO> team1List;
    private final List<SummonerDTO> team2List;

    private final String team1AvgTierRank;
    private final String team2AvgTierRank;
    private final String gameAvgTierRank;

    //TODO 인자로 간단하게 받고, 여기서 알잘딱으로 반환하자.(= TeamBalanceTeamInfoDTO삭제하고 여기서 계산해서 전달하기)
    public TeamBalanceResponseDTO(List<SummonerDTO> team1List, List<SummonerDTO> team2List) {
        this.team1List = team1List;
        this.team2List = team2List;

        // team1
        float team1SumMmr = team1List.stream().mapToInt(SummonerDTO::getMmr).sum();
        int team1AvgMmr = Math.round(team1SumMmr / 5);
        this.team1AvgTierRank = mmrToTierRank(team1AvgMmr);

        // team2
        float team2SumMmr = team1List.stream().mapToInt(SummonerDTO::getMmr).sum();
        int team2AvgMmr = Math.round(team2SumMmr / 5);
        this.team2AvgTierRank = mmrToTierRank(team2AvgMmr);

        // gameAvg
        float gameSumMmr = team1SumMmr + team2SumMmr;
        int gameAvgMmr = Math.round(gameSumMmr / 10);
        this.gameAvgTierRank = mmrToTierRank(gameAvgMmr);

    }

    private String mmrToTierRank(int mmr) {
        if(mmr == 0) return "UNRANKED";
        if(mmr == 29) return "MASTER";
        if(mmr == 30) return "GRANDMASTER";
        if(mmr == 31) return "CHALLENGER";

        String[] tiers = {"IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM", "EMERALD", "DIAMOND"};
        String[] divisions = {"IV", "III", "II", "I"};

        if (mmr >= 1 && mmr <= 28) {
            int tierIndex = (mmr - 1) / 4;
            int divisionIndex = (mmr - 1) % 4;
            return tiers[tierIndex] + " " + divisions[divisionIndex];
        }

        return "ERROR: 관리자에게 문의바랍니다.";
    }

}
