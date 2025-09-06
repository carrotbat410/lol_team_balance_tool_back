package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.TeamAssignMode;
import com.carrotbat410.lol_team_balance_tool.dto.TeamBalanceRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.utils.GoldenBalanceProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrotbat410.lol_team_balance_tool.dto.response.TeamBalanceResponseDTO;

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

        if(teamAssignMode == GOLDEN_BALANCE) {
            GoldenBalanceProcessor goldenBalanceProcessor = new GoldenBalanceProcessor(teamBalanceRequestDTO.getTeam1List(), teamBalanceRequestDTO.getTeam2List(), teamBalanceRequestDTO.getNoTeamList());
            result = new TeamBalanceResponseDTO(goldenBalanceProcessor.finalTeam1List, goldenBalanceProcessor.finalTeam2List);
        }else if(teamAssignMode == BALANCE) {
            // TODO: Implement BALANCE mode
            throw new UnsupportedOperationException("Not implemented yet");
        }else if(teamAssignMode == RANDOM) {
            // TODO: Implement RANDOM mode
            throw new UnsupportedOperationException("Not implemented yet");
        }

        return new SuccessResponseDTO<>("ok", result);
    }

}