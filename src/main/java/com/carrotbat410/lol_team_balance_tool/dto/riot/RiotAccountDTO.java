package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiotAccountDTO {
    private String puuid;
    private String gameName;
    private String tagLine;

    @Override
    public String toString() {
        return "RiotAccountDTO{" +
                "puuid='" + puuid + '\'' +
                ", gameName='" + gameName + '\'' +
                ", tagLine='" + tagLine + '\'' +
                '}';
    }
}
