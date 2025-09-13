package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "TmpHealthCheckController", description = "임시 헬스체크 컨트롤러")
public class TmpHealthCheckController {

    private final Environment env;

    @GetMapping("/tmpHealthCheck")
    @Operation(summary = "임시 헬스체크", description = "임시 헬스체크용 컨트롤러입니다.")
    public SuccessResponseDTO<Void> tmpHealthCheck() {
        String port = env.getProperty("local.server.port");
        log.info("Health check on port: {}", port);

        return new SuccessResponseDTO<>("tmp health check ok on port " + port);
    }
}
