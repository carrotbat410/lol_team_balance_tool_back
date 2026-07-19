package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.VisitTrackRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.VisitTrackResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.VisitorLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VisitController {

    private final VisitorLogService visitorLogService;

    @PostMapping("/visits")
    public SuccessResponseDTO<VisitTrackResponseDTO> trackVisit(@Valid @RequestBody VisitTrackRequestDTO request) {
        return new SuccessResponseDTO<>("visit tracked", visitorLogService.trackVisit(request));
    }
}
