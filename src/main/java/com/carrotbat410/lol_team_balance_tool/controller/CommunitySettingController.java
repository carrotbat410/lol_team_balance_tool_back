package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.CommunitySettingUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunitySettingResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.CommunitySettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "CommunitySettingController", description = "커뮤니티 공개 설정 컨트롤러")
public class CommunitySettingController {

    private final CommunitySettingService communitySettingService;

    @GetMapping("/community/settings")
    @Operation(summary = "커뮤니티 공개 설정", description = "일반 사용자에게 커뮤니티를 공개할지 조회합니다.")
    public SuccessResponseDTO<CommunitySettingResponseDTO> setting() {
        return new SuccessResponseDTO<>("community setting", communitySettingService.getSetting());
    }

    @PatchMapping("/admin/community/settings")
    @Operation(summary = "커뮤니티 공개 설정 변경", description = "관리자가 커뮤니티 공개 여부를 변경합니다.")
    public SuccessResponseDTO<CommunitySettingResponseDTO> updateSetting(
            @Valid @RequestBody CommunitySettingUpdateRequestDTO request
    ) {
        return new SuccessResponseDTO<>("community setting updated", communitySettingService.updateSetting(request));
    }
}
