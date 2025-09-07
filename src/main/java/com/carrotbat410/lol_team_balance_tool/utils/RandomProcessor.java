package com.carrotbat410.lol_team_balance_tool.utils;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomProcessor {
    private final List<SummonerDTO> team1List;
    private final List<SummonerDTO> team2List;
    private final List<SummonerDTO> noTeamList;

    public List<SummonerDTO> finalTeam1List = new ArrayList<>();
    public List<SummonerDTO> finalTeam2List = new ArrayList<>();

    public RandomProcessor(
            List<SummonerDTO> team1List, List<SummonerDTO> team2List, List<SummonerDTO> noTeamList
    ) {
        this.team1List = team1List;
        this.team2List = team2List;
        this.noTeamList = noTeamList;
        Collections.shuffle(noTeamList);

        finalTeam1List.addAll(team1List);
        finalTeam2List.addAll(team2List);

        int team1NeedCnt = 5 - team1List.size();

        for(int i = 0; i < team1NeedCnt; i++) {
            finalTeam1List.add(noTeamList.get(i));
        }
        for(int i = team1NeedCnt; i < noTeamList.size(); i++) {
            finalTeam2List.add(noTeamList.get(i));
        }
    }
}
