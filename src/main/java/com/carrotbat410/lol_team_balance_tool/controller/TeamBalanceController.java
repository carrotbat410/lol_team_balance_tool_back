package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.TeamAssignMode;
import com.carrotbat410.lol_team_balance_tool.dto.TeamBalanceRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.utils.GoldenBalanceProcessor;
import com.carrotbat410.lol_team_balance_tool.utils.RandomProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrotbat410.lol_team_balance_tool.dto.response.TeamBalanceResponseDTO;

import java.util.List;

import static com.carrotbat410.lol_team_balance_tool.dto.TeamAssignMode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamBalanceController {

    @PostMapping("/balance")
    public SuccessResponseDTO<TeamBalanceResponseDTO> generateTeamBalanceResult(@RequestBody @Validated TeamBalanceRequestDTO teamBalanceRequestDTO) {

        TeamAssignMode teamAssignMode = teamBalanceRequestDTO.getTeamAssignMode();
        TeamBalanceResponseDTO result = null;

        List<SummonerDTO> team1List = teamBalanceRequestDTO.getTeam1List();
        List<SummonerDTO> team2List = teamBalanceRequestDTO.getTeam2List();
        List<SummonerDTO> noTeamList = teamBalanceRequestDTO.getNoTeamList();

        //* GoldenBalanceProcessor()도 그렇고, TeamBalanceResponseDTO()도 그렇고
        //* 인자로 team1List, team2List, noTeamList이런식으로 받는게아닌, teamBalanceRequestDTO 하나만 넘기는 방식이 더 좋은코드려나
        if(teamAssignMode == GOLDEN_BALANCE) {
            GoldenBalanceProcessor goldenBalanceProcessor = new GoldenBalanceProcessor(team1List, team2List, noTeamList);
            result = new TeamBalanceResponseDTO(goldenBalanceProcessor.finalTeam1List, goldenBalanceProcessor.finalTeam2List);
        }else if(teamAssignMode == BALANCE) {
            // TODO: Implement BALANCE mode
            throw new UnsupportedOperationException("Not implemented yet");
        }else if(teamAssignMode == RANDOM) {
            RandomProcessor randomProcessor = new RandomProcessor(team1List, team2List, noTeamList);
            result = new TeamBalanceResponseDTO(randomProcessor.finalTeam1List, randomProcessor.finalTeam2List);
        }

        return new SuccessResponseDTO<>("ok", result);
    }

}