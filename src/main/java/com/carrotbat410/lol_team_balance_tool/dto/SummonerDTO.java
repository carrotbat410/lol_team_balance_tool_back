package com.carrotbat410.lol_team_balance_tool.dto;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@Schema(description = "소환사 정보 DTO")
public class SummonerDTO {
    @Schema(description = "고유 번호", example = "1")
    private final Long no;
    @Schema(description = "소환사 이름", example = "hide on bush")
    private final String summonerName;
    @Schema(description = "태그 라인", example = "KR1")
    private final String tagLine;
    @Schema(description = "티어", example = "CHALLENGER")
    private final Tier tier;
    @Schema(description = "랭크", example = "1")
    private final Integer rank;
    @Schema(description = "MMR", example = "31")
    private final int mmr;
    @Schema(description = "소환사 레벨", example = "500")
    private final int summonerLevel;
    @Schema(description = "승리 횟수", example = "100")
    private final int wins;
    @Schema(description = "패배 횟수", example = "50")
    private final int losses;
    @Schema(description = "프로필 아이콘 ID", example = "1234")
    private final int profileIconId;
    @Schema(description = "생성일")
    private final LocalDateTime created_at;
    @Schema(description = "수정일")
    private final LocalDateTime updated_at;


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
                .created_at(summonerEntity.getCreated_at())
                .updated_at(summonerEntity.getUpdated_at())
                .build();
    }

}