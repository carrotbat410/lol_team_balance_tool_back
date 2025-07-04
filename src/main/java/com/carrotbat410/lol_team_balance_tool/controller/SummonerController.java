package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.service.SummonerService;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SummonerController {

    private final SummonerService summonerService;

//    public SummonerController(SummonerService summonerService) {
//        this.summonerService = summonerService;
//    }

    @GetMapping("/mySummoners")
    public String getMySummonerList() {

//        SummonerDTO[] summonerDTOs = summonerService.getMySummoner();
//        return summonerDTOs;
        return "hi??";
    }

    @PostMapping("/summoner")
    public SuccessResponseDTO<Void> addSummonerWithSummonerNameAndTagLine(@RequestBody @Validated AddSummonerRequestDTO addSummonerRequestDTO) {

        String summonerName = addSummonerRequestDTO.getSummonerName();
        String tagLine = addSummonerRequestDTO.getTagLine();

        summonerService.saveSummoner(addSummonerRequestDTO);

        return new SuccessResponseDTO<>();
    }
}
