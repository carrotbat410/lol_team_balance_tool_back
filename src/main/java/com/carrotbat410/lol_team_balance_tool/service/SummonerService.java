package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final SummonerRepository summonerRepository;


    public SummonerEntity saveSummoner(SummonerEntity summonerEntity) {
        // 필요한값들: 아이디(토큰에서뽑기) / summonerName , tagLine
//        boolean isExistSummoner =

        return summonerRepository.save(summonerEntity); //save 메서드는 insert + update를 모두 처리할 수 있음. id가 없으면 insert, id가 있으면 update로 동작.
    }

//    public String getMySummoners() {
//        return "tmp ok";
//    }

}
