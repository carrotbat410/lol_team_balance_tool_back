package com.carrotbat410.lol_team_balance_tool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "소환사 정보 수정 요청 DTO")
public class UpdateSummonerReqeustDTO {

    @NotNull(message = "소환사이름을 입력해주세요.")
    @Length(min = 1, max = 30, message = "올바르지 않은 소환사이름 양식입니다.")
    @Schema(description = "소환사 이름", example = "hide on bush")
    private final String summonerName;

    @Length(max = 30, message = "올바르지 않은 태그라인 양식입니다.")
    @Schema(description = "태그 라인", example = "KR1")
    private final String tagLine;
}
