package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.JoinDTO;
import com.carrotbat410.lol_team_balance_tool.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {

    @Autowired//TODO 모든 Autowired -> 생성자 주입 방식으로 바꾸기
    private JoinService joinService;

    @PostMapping("/join")
    public String Join(@RequestBody JoinDTO joinDTO) {

        //TODO 입력값 검증 로직 추가하기(@Validated 이용)

        String result = joinService.joinProcess(joinDTO);

        //TODO 반환 객체 만들어서 통일시키기?
        if(result.equals("yes")) return "success";
        else return "already exists";
    }
}
