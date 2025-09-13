package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.JoinDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "JoinController", description = "회원가입 컨트롤러")
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "사용자 ID와 비밀번호로 회원가입을 진행합니다.")
    public SuccessResponseDTO<Void> Join(@RequestBody JoinDTO joinDTO) {

        //TODO 입력값 검증 로직 추가하기(@Validated 이용)

        joinService.joinProcess(joinDTO);

        return new SuccessResponseDTO<>("ok");
    }
}
