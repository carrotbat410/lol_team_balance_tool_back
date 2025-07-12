package com.carrotbat410.lol_team_balance_tool.dto.riot;

import lombok.*;

@Getter
@Setter
//@NoArgsConstructor
@RequiredArgsConstructor
public class RiotAccountDTO {
    private final String puuid;
    private final String gameName;
    private final String tagLine;

    @Override
    public String toString() {
        return "RiotAccountDTO{" +
                "puuid='" + puuid + '\'' +
                ", gameName='" + gameName + '\'' +
                ", tagLine='" + tagLine + '\'' +
                '}';
    }
}
