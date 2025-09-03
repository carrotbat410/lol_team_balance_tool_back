package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.TeamAssignMode;
import com.carrotbat410.lol_team_balance_tool.dto.TeamBalanceRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.carrotbat410.lol_team_balance_tool.dto.TeamAssignMode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamBalanceController {

//    private final TeamBalanceService teamBalanceService;

    @PostMapping("/balance")
    public SuccessResponseDTO<Void> generateTeamBalanceResult(@RequestBody @Validated TeamBalanceRequestDTO teamBalanceRequestDTO) {

        TeamAssignMode teamAssignMode = teamBalanceRequestDTO.getTeamAssignMode();

        if(teamAssignMode == GOLDEN_BALANCE) {
            System.out.println("1");
        }else if(teamAssignMode == BALANCE) {
            System.out.println("2");
        }else if(teamAssignMode == RANDOM) {
            System.out.println("3");
        }


        return new SuccessResponseDTO<>("ok");
    }

}