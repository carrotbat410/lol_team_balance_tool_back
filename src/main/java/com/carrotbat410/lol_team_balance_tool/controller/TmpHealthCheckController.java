package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "TmpHealthCheckController", description = "임시 헬스체크 컨트롤러")
public class TmpHealthCheckController {

    @GetMapping("/tmpHealthCheck")
    @Operation(summary = "임시 헬스체크", description = "임시 헬스체크용 컨트롤러입니다.")
    public SuccessResponseDTO<Void> tmpHealthCheck() {

        return new SuccessResponseDTO<>("tmp health check ok");
    }
}
