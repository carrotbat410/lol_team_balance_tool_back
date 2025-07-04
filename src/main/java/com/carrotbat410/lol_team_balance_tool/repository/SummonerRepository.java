package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SummonerRepository extends JpaRepository<SummonerEntity, Long> {

    //Riot Account API에 summonerName -> 띄어쓰기 구분 x, 영문 대소문자 구분 xs
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM SummonerEntity  s " +
            "WHERE s.userId = :userId " +
            "AND REPLACE(UPPER(s.summonerName), ' ', '') = REPLACE(UPPER(:summonerName), ' ', '') " +
            "AND UPPER(s.tagLine) = UPPER(:tagLine)")
    boolean existsByUserIdAndSummonerNameAndTagLineIgnoreCaseAndNoSpaces(@Param("userId") String userId, @Param("summonerName") String summonerName, @Param("tagLine") String tagLine);

}
