package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.ChangePasswordRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "AccountController", description = "계정 관련 컨트롤러")
public class AccountController {

    private final AccountService accountService;

    @PatchMapping("/account/password")
    @Operation(summary = "비밀번호 변경", description = "현재 로그인된 사용자의 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다.")
    public SuccessResponseDTO<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        accountService.changePassword(request);
        return new SuccessResponseDTO<>("password changed");
    }

    @DeleteMapping("/account")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 비밀번호를 확인한 뒤 계정을 탈퇴합니다.")
    public SuccessResponseDTO<Void> deleteMyAccount(@Valid @RequestBody DeleteAccountRequestDTO request) {
        accountService.deleteMyAccount(request);
        return new SuccessResponseDTO<>("account deleted");
    }
}
