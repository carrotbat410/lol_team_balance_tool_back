package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunitySettingUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunitySettingResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunitySettingEntity;
import com.carrotbat410.lol_team_balance_tool.repository.CommunitySettingRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

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
        if (!SecurityUtils.isCurrentUserOperator()) {
            throw new AccessDeniedException("운영자만 커뮤니티 공개 설정을 변경할 수 있습니다.");
        }

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
