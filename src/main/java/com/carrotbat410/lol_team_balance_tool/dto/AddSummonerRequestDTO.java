package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSummonerRequestDTO {

    @NotNull(message = "소환사이름을 입력해주세요.")
    @Length(min = 1, max = 30, message = "올바르지 않은 소환사이름 양식입니다.")
    private String summonerName;

    @Length(max = 30, message = "올바르지 않은 태그라인 양식입니다.")
    private String tagLine;

}
