package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.CommunitySettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunitySettingRepository extends JpaRepository<CommunitySettingEntity, Integer> {
}
