package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.UpdateSummonerReqeustDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.service.SummonerService;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "SummonerController", description = "소환사 관련 컨트롤러")
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoners")
    @Operation(summary = "내 소환사 목록 조회", description = "현재 로그인된 사용자의 소환사 목록을 조회합니다.")
    public SuccessResponseDTO<List<SummonerDTO>> getMySummoners() {
        List<SummonerDTO> summoners = summonerService.findSummoners();
        return new SuccessResponseDTO<>("ok",summoners);
    }

    @PostMapping("/summoner")
    @Operation(summary = "소환사 추가", description = "소환사 이름과 태그라인으로 소환사를 추가합니다.")
    public SuccessResponseDTO<SummonerDTO> addSummonerWithSummonerNameAndTagLine(@RequestBody @Validated AddSummonerRequestDTO addSummonerRequestDTO) {

        SummonerDTO summonerDTO = summonerService.saveSummoner(addSummonerRequestDTO);

        return new SuccessResponseDTO<>("ok", summonerDTO);
    }

    @PatchMapping("/summoner")
    @Operation(summary = "소환사 정보 수정", description = "소환사 이름과 태그라인으로 등록된 소환사의 정보를 업데이트합니다.")
    public SuccessResponseDTO<Void> updateSummoner(@RequestBody @Validated UpdateSummonerReqeustDTO updateSummonerReqeustDTO) {

        summonerService.updateSummoner(updateSummonerReqeustDTO);

        return new SuccessResponseDTO<>();
    }
}
