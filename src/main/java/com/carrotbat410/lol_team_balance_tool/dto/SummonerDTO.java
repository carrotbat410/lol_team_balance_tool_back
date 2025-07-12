package com.carrotbat410.lol_team_balance_tool.dto;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class SummonerDTO {
    private final Long no;
    private final String summonerName;
    private final String tagLine;
    private final Tier tier;
    private final Integer rank;
    private final int mmr;
    private final int summonerLevel;
    private final int wins;
    private final int losses;
    private final int profileIconId;

    public static SummonerDTO fromEntity(SummonerEntity summonerEntity) {
        //* builder 사용 후기: 순서에 맞게 필드값들 안넣어도 되고, 필드명에 의존하여 맵핑이 이루어져서 가독성도 좋은듯
        return SummonerDTO.builder()
                .no(summonerEntity.getNo())
                .summonerName(summonerEntity.getSummonerName())
                .tagLine(summonerEntity.getTagLine())
                .tier(summonerEntity.getTier())
                .rank(summonerEntity.getRank1())
                .mmr(summonerEntity.getMmr())
                .summonerLevel(summonerEntity.getLevel())
                .wins(summonerEntity.getWins())
                .losses(summonerEntity.getLosses())
                .profileIconId(summonerEntity.getIconId())
                .build();
    }

}