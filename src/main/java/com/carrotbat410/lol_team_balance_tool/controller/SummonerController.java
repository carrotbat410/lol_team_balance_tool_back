package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.service.SummonerService;
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
        System.out.println("summonerName:" + summonerName + " | tagLine: " + tagLine);

        SummonerEntity tmpSummonerEntity = new SummonerEntity(null, "test1", "통티모바베큐3", "0410", "GOLD", 1, 11, 120, 90, 10, 230);
        summonerService.saveSummoner(tmpSummonerEntity);

        tmpSummonerEntity.getCreated_at();
        return new SuccessResponseDTO<>();
    }
}
