package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "AccountController", description = "계정 관련 컨트롤러")
public class AccountController {

    private final AccountService accountService;

    @DeleteMapping("/account")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 비밀번호를 확인한 뒤 계정을 탈퇴합니다.")
    public SuccessResponseDTO<Void> deleteMyAccount(@Valid @RequestBody DeleteAccountRequestDTO request) {
        accountService.deleteMyAccount(request);
        return new SuccessResponseDTO<>("account deleted");
    }
}
