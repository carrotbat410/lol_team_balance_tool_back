package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateSummonerReqeustDTO {

    @NotNull(message = "소환사이름을 입력해주세요.")
    @Length(min = 1, max = 30, message = "올바르지 않은 소환사이름 양식입니다.")
    private final String summonerName;

    @Length(max = 30, message = "올바르지 않은 태그라인 양식입니다.")
    private final String tagLine;
}
