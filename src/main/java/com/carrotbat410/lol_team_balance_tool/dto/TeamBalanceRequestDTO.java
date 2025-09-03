package com.carrotbat410.lol_team_balance_tool.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description =  "팀 결과 생성 요청 DTO")
public class TeamBalanceRequestDTO {

    private TeamAssignMode teamAssignMode;
    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private List<SummonerDTO> noTeamList;

    @AssertTrue(message = "필요 인원은 10명 입니다.")
    public boolean isSummonerSizeValid() {
        int totalSummonerSize = team1List.size() + team2List.size() + noTeamList.size();
        return totalSummonerSize == 10;
    }

    @AssertTrue(message = "각 팀 최대 인원은 5명입니다.")
    public boolean isTeamSizeValid() {
        return team1List.size() <= 5 && team2List.size() <= 5;
    }

}
