package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.DataConflictException;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final SummonerRepository summonerRepository;


    public void saveSummoner(AddSummonerRequestDTO addSummonerRequestDTO) {

        //1. 요청에 필요한 값들 가지고 있기.
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        String summonerName = addSummonerRequestDTO.getSummonerName().trim();
        String tagLine = addSummonerRequestDTO.getTagLine().trim();
        System.out.println(userId+summonerName+tagLine);

        //TODO 2. 먼저 DB에 같은userId가 이미 추가했는지 확인하기
        //TODO 특히, Summoner에서 조회/Riot API에서 조회할떄 nickname,tagLine 조회 조건 다른거 확인해야함.

        boolean isExist = summonerRepository.existsByUserIdAndSummonerNameAndTagLine(userId, summonerName, tagLine);
        if(isExist) throw new DataConflictException("이미 데이터가 존재합니다.");

        //TODO 3.(없다면) Riot API이용해서 정보 가져오기.
        SummonerEntity tmpSummonerEntity = new SummonerEntity(null, "test1", "통티모바베큐", "0410", "GOLD", 1, 11, 120, 90, 10, 230);

        summonerRepository.save(tmpSummonerEntity);
    }

//    public String getMySummoners() {
//        return "tmp ok";
//    }

}
