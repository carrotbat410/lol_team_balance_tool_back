package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.AdminUserRoleUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.AdminUserResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.VisitSummaryResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.AdminUserService;
import com.carrotbat410.lol_team_balance_tool.service.VisitorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "AdminController", description = "관리자 기능 컨트롤러")
public class AdminController {

    private final VisitorLogService visitorLogService;
    private final AdminUserService adminUserService;

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

    @GetMapping("/admin/users")
    @Operation(summary = "회원 목록", description = "관리자용 회원 목록 API")
    public SuccessResponseDTO<List<AdminUserResponseDTO>> users() {
        return new SuccessResponseDTO<>("user list", adminUserService.getUsers());
    }

    @PatchMapping("/admin/users/{userNo}/role")
    @Operation(summary = "회원 권한 변경", description = "관리자용 회원 권한 변경 API")
    public SuccessResponseDTO<AdminUserResponseDTO> updateUserRole(
            @PathVariable int userNo,
            @Valid @RequestBody AdminUserRoleUpdateRequestDTO request
    ) {
        return new SuccessResponseDTO<>("role updated", adminUserService.updateRole(userNo, request));
    }
}
