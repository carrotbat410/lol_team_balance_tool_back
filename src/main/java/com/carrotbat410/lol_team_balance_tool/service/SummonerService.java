package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotAccountDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotLeagueEntryDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotSummonerDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.DataConflictException;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.riot.RiotApiClient;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final SummonerRepository summonerRepository;
    private final RiotApiClient riotApiClient;


    public void saveSummoner(AddSummonerRequestDTO addSummonerRequestDTO) {

        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        String summonerName = addSummonerRequestDTO.getSummonerName().trim();
        String tagLine = addSummonerRequestDTO.getTagLine().trim();

        boolean isExist = summonerRepository.existsByUserIdAndSummonerNameAndTagLine(userId, summonerName, tagLine);
        if (isExist) {
            throw new DataConflictException("이미 등록된 소환사입니다.");
        }

        RiotAccountDTO account = riotApiClient.fetchAccountByRiotId(summonerName, tagLine).block();
//        System.out.println("-----------------------------------------");
//        System.out.println("getGameName:"+account.getGameName());
//        System.out.println("getPuuid:"+account.getPuuid());
        System.out.println("account::"+account);
        System.out.println("account.toString()::"+account.toString());

        if (account == null) {
            throw new DataConflictException("소환사 정보를 찾을 수 없습니다.");
        }

        RiotSummonerDTO summoner = riotApiClient.fetchSummonerByPuuid(account.getPuuid()).block();

        if (summoner == null) {
            throw new DataConflictException("소환사 정보를 찾을 수 없습니다.");
        }

        RiotLeagueEntryDTO[] leagueEntries = riotApiClient.fetchLeagueEntryByPuuid(summoner.getPuuid()).block();
        RiotLeagueEntryDTO soloRank = findSoloRank(leagueEntries);

        SummonerEntity summonerEntity = createSummonerEntity(userId, account, summoner, soloRank);
        summonerRepository.save(summonerEntity);
    }

    private RiotLeagueEntryDTO findSoloRank(RiotLeagueEntryDTO[] leagueEntries) {
        if (leagueEntries == null) {
            return null;
        }
        return Arrays.stream(leagueEntries)
                .filter(entry -> "RANKED_SOLO_5x5".equals(entry.getQueueType()))
                .findFirst()
                .orElse(null);
    }

    private SummonerEntity createSummonerEntity(String userId, RiotAccountDTO account, RiotSummonerDTO summoner, RiotLeagueEntryDTO soloRank) {
        return new SummonerEntity(
                null,
                userId,
                account.getGameName(),
                account.getTagLine(),
                Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getTier).orElse("UNRANKED"),
                Optional.ofNullable(soloRank).map(this::convertRankToInt).orElse(0),
                0, // MMR은 별도 API 필요 (현재 API 스펙에 없음)
                (int) summoner.getSummonerLevel(),
                Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getWins).orElse(0),
                Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getLosses).orElse(0),
                summoner.getProfileIconId()
        );
    }

    private int convertRankToInt(RiotLeagueEntryDTO soloRank) {
        return switch (soloRank.getRank()) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            default -> 0;
        };
    }
}

