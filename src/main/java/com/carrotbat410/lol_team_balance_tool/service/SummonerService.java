package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.UpdateSummonerReqeustDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotAccountDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotLeagueEntryDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotSummonerDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.DataConflictException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.riot.RiotApiClient;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final SummonerRepository summonerRepository;
    private final RiotApiClient riotApiClient;

    public List<SummonerDTO> findSummoners() {
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        List<SummonerEntity> summonerEntityList = summonerRepository.findByUserId(userId);

        return summonerEntityList.stream()
                .map(SummonerDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }


    public SummonerDTO saveSummoner(AddSummonerRequestDTO addSummonerRequestDTO) {

        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        String summonerName = addSummonerRequestDTO.getSummonerName().trim();
        String tagLine = addSummonerRequestDTO.getTagLine().trim();

        boolean isExist = summonerRepository.existsByUserIdAndSummonerNameAndTagLineIgnoreCaseAndNoSpaces(userId, summonerName, tagLine);
        if (isExist) throw new DataConflictException("이미 등록된 소환사입니다.");

        RiotAccountDTO account = riotApiClient.fetchAccountByRiotId(summonerName, tagLine).block();
        RiotSummonerDTO summoner = riotApiClient.fetchSummonerByPuuid(account.getPuuid()).block();
        RiotLeagueEntryDTO soloRank = riotApiClient.fetchSoloRankLeagueEntryByPuuid(account.getPuuid()).block();

        SummonerEntity summonerEntity = createSummonerEntity(null, userId, account, summoner, soloRank);
        SummonerEntity save = summonerRepository.save(summonerEntity);
        return SummonerDTO.fromEntity(save);
    }

    public void updateSummoner(UpdateSummonerReqeustDTO updateSummonerReqeustDTO){
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        String summonerName = updateSummonerReqeustDTO.getSummonerName().trim();
        String tagLine = updateSummonerReqeustDTO.getTagLine().trim();

        Long summonerNo = summonerRepository.findNoByUserIdAndSummonerNameAndTagLineIgnoreCaseAndNoSpaces(userId, summonerName, tagLine)
                .orElseThrow(() -> new NotFoundDataException("존재하지 않는 소환사입니다."));

        RiotAccountDTO account = riotApiClient.fetchAccountByRiotId(summonerName, tagLine).block();
        RiotSummonerDTO summoner = riotApiClient.fetchSummonerByPuuid(account.getPuuid()).block();
        RiotLeagueEntryDTO soloRank = riotApiClient.fetchSoloRankLeagueEntryByPuuid(account.getPuuid()).block();

        SummonerEntity summonerEntity = createSummonerEntity(summonerNo, userId, account, summoner, soloRank);
        summonerRepository.save(summonerEntity);
    }

    private SummonerEntity createSummonerEntity(Long summonerNo, String userId, RiotAccountDTO account, RiotSummonerDTO summoner, RiotLeagueEntryDTO soloRank) {
        String tierStr = Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getTier).orElse("UNRANKED");
        Tier tier = Tier.valueOf(tierStr);
        String rank = Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getRank).orElse(null);
        int mmr = calculateMmr(tier, rank);

        return new SummonerEntity(
                summonerNo,
                userId,
                account.getGameName(),
                account.getTagLine(),
                tier,
                Optional.ofNullable(soloRank).map(this::convertRankToInt).orElse(0),
                mmr,
                (int) summoner.getSummonerLevel(),
                Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getWins).orElse(0),
                Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getLosses).orElse(0),
                summoner.getProfileIconId()
        );
    }

    private int calculateMmr(Tier tier, String rank) {
        if (tier == null) {
            return 0;
        }

        switch (tier) {
            case UNRANKED:
                return 0;
            case MASTER:
                return 29;
            case GRANDMASTER:
                return 30;
            case CHALLENGER:
                return 31;
        }

        int tierBase = switch (tier) {
            case IRON -> 0;
            case BRONZE -> 4;
            case SILVER -> 8;
            case GOLD -> 12;
            case PLATINUM -> 16;
            case EMERALD -> 20;
            case DIAMOND -> 24;
            default -> 0;
        };

        int rankValue = rank != null ? switch (rank) {
            case "IV" -> 1;
            case "III" -> 2;
            case "II" -> 3;
            case "I" -> 4;
            default -> 0;
        } : 0;

        return tierBase + rankValue;
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

