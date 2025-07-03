package com.carrotbat410.lol_team_balance_tool.dto;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SummonerDTO {
    private final Long no;
    private final String summonerName;
    private final String tagLine;
    private final String tier;
    private final Integer rank;
    private final int mmr;
    private final int level;
    private final int wins;
    private final int losses;
    private final int iconId;
//    private final LocalDateTime updatedAt;

//    public static SummonerDTO from(SummonerEntity summonerEntity) {
//
//        return new SummonerDTO(
//                summonerEntity.getNo(), summonerEntity.getSummonerName(), summonerEntity.getTagLine(), summonerEntity.getTier(), summonerEntity.getRank1(),
//                summonerEntity.getMmr(), summonerEntity.getLevel(), summonerEntity.getWins(), summonerEntity.getLosses(),summonerEntity.getIconId(), summonerEntity.getLevel(), );
//    }

//    @QueryProjection
    public SummonerDTO(Long no, String summonerName, String tagLine, String tier, Integer rank, int mmr, int level, int wins, int losses, int iconId) {
        this.no = no;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.tier = tier;
        this.rank = rank;
        this.mmr = mmr;
        this.level = level;
        this.wins = wins;
        this.losses = losses;
        this.iconId = iconId;
    }

}