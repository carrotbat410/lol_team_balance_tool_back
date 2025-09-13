package com.carrotbat410.lol_team_balance_tool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "AdminController", description = "관리자 기능 컨트롤러")
public class AdminController {

    @GetMapping("/admin")
    @Operation(summary = "관리자 페이지 접속 테스트", description = "관리자 권한을 가진 사용자만 접속 가능한 테스트용 API")
    public String admin() {
        return "admin";
    }
}
