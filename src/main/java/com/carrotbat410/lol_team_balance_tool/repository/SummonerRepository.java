package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummonerRepository extends JpaRepository<SummonerEntity, Long> {
    boolean existsByUserIdAndSummonerNameAndTagLine(String userId, String summonerName, String tagLine);
}
