package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.VisitSummaryResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.VisitorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "AdminController", description = "관리자 기능 컨트롤러")
public class AdminController {

    private final VisitorLogService visitorLogService;

    @GetMapping("/admin")
    @Operation(summary = "관리자 페이지 접속 테스트", description = "관리자 권한을 가진 사용자만 접속 가능한 테스트용 API")
    public String admin() {
        return "admin";
    }

    @GetMapping("/admin/visits/summary")
    @Operation(summary = "방문자 통계", description = "관리자용 방문자 통계 API")
    public SuccessResponseDTO<VisitSummaryResponseDTO> visitSummary(
            @RequestParam(defaultValue = "30") int days
    ) {
        return new SuccessResponseDTO<>("visit summary", visitorLogService.getVisitSummary(days));
    }
}
