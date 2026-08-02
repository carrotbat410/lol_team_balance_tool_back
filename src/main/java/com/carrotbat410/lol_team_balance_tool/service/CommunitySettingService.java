package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunitySettingUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunitySettingResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunitySettingEntity;
import com.carrotbat410.lol_team_balance_tool.repository.CommunitySettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunitySettingService {

    private static final int COMMUNITY_SETTING_ID = 1;

    private final CommunitySettingRepository communitySettingRepository;

    public CommunitySettingResponseDTO getSetting() {
        CommunitySettingEntity setting = getOrCreateSetting();
        return new CommunitySettingResponseDTO(setting.isVisibleToUsers(), setting.getNoticeDisplayCount());
    }

    @Transactional
    public CommunitySettingResponseDTO updateSetting(CommunitySettingUpdateRequestDTO request) {
        CommunitySettingEntity setting = getOrCreateSetting();
        setting.setVisibleToUsers(Boolean.TRUE.equals(request.getVisibleToUsers()));
        setting.setNoticeDisplayCount(request.getNoticeDisplayCount());

        CommunitySettingEntity savedSetting = communitySettingRepository.save(setting);
        return new CommunitySettingResponseDTO(savedSetting.isVisibleToUsers(), savedSetting.getNoticeDisplayCount());
    }

    public boolean isVisibleToUsers() {
        return getOrCreateSetting().isVisibleToUsers();
    }

    private CommunitySettingEntity getOrCreateSetting() {
        return communitySettingRepository.findById(COMMUNITY_SETTING_ID)
                .orElseGet(() -> {
                    CommunitySettingEntity setting = new CommunitySettingEntity();
                    setting.setId(COMMUNITY_SETTING_ID);
                    setting.setVisibleToUsers(false);
                    setting.setNoticeDisplayCount(2);
                    return communitySettingRepository.save(setting);
                });
    }
}
