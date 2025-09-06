package com.carrotbat410.lol_team_balance_tool.utils;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GoldenBalanceProcessor {
    private final List<SummonerDTO> team1List;
    private final List<SummonerDTO> team2List;
    private final List<SummonerDTO> noTeamList;

    private int totalMmrSum;
    private int mmrDiff = Integer.MAX_VALUE;
    private final int n; //n개 중에
    private final int r; //r개 뽑기
    private final Integer[] selectedTeam1IdxList;

    public List<SummonerDTO> finalTeam1List = new ArrayList<>();
    public List<SummonerDTO> finalTeam2List = new ArrayList<>();


    public GoldenBalanceProcessor(
            List<SummonerDTO> team1List, List<SummonerDTO> team2List, List<SummonerDTO> noTeamList
    ) {
        this.team1List = team1List;
        this.team2List = team2List;
        this.noTeamList = noTeamList;
        Collections.shuffle(noTeamList);

        for (SummonerDTO summonerDTO: team1List) {
            finalTeam1List.add(summonerDTO);
            totalMmrSum += summonerDTO.getMmr();
        }
        for (SummonerDTO summonerDTO: team2List) {
            finalTeam2List.add(summonerDTO);
            totalMmrSum += summonerDTO.getMmr();
        }
        for (SummonerDTO summonerDTO: noTeamList) {
            totalMmrSum += summonerDTO.getMmr();
        }
        n = noTeamList.size();
        r = 5 - team1List.size();
        selectedTeam1IdxList = new Integer[r];

        int initTeam1MmrSum = 0;

        for (SummonerDTO team1Member : team1List) {
            initTeam1MmrSum += team1Member.getMmr();
        }

        DFS(0, 0, initTeam1MmrSum);


    }

    private void DFS(int L, int s, int tmpTeam1MmrSum) {
        if (L == r) {
            int tmpTeam2MmrSum = totalMmrSum - tmpTeam1MmrSum;
            int tmpMmrDiff = Math.abs(tmpTeam1MmrSum - tmpTeam2MmrSum);

            //#1. mmr 차이 적은 결과 발견하면 결과 저장하기.
            if (tmpMmrDiff < mmrDiff) {

                //#2. newFinalTeam1List 만들기
                List<SummonerDTO> newFinalTeam1List = new ArrayList<>(team1List);
                HashMap<Integer, SummonerDTO> clonedNoTeamList = new HashMap<>();
                for (int i = 0; i < n; i++) clonedNoTeamList.put(i, noTeamList.get(i));

                for (Integer i : selectedTeam1IdxList) {
                    newFinalTeam1List.add(clonedNoTeamList.get(i));
                    clonedNoTeamList.remove(i);
                }

                //#2. newFinalTeam2List 만들기
                List<SummonerDTO> newFinalTeam2List = new ArrayList<>(team2List);
                newFinalTeam2List.addAll(clonedNoTeamList.values());

                //#3. 필드에 현재값으로 저장하기
                mmrDiff = tmpMmrDiff;
                finalTeam1List = newFinalTeam1List;
                finalTeam2List = newFinalTeam2List;
            }

        } else {
            for (int i = s; i < n; i++) {
                SummonerDTO tmpSummoner = noTeamList.get(i);
                selectedTeam1IdxList[L] = i;
                DFS(L + 1, i + 1, tmpTeam1MmrSum + tmpSummoner.getMmr());
            }
        }
    }
}
