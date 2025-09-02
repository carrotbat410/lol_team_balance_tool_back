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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final SummonerRepository summonerRepository;
    private final RiotApiClient riotApiClient;

    public Page<SummonerDTO> findSummoners(Pageable pageable) {
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        Page<SummonerEntity> summonerEntityPage = summonerRepository.findByUserId(userId, pageable);

        return summonerEntityPage.map(SummonerDTO::fromEntity);
    }


    public SummonerDTO saveSummoner(AddSummonerRequestDTO addSummonerRequestDTO) {

        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        String summonerName = addSummonerRequestDTO.getSummonerName().trim();
        String tagLine = addSummonerRequestDTO.getTagLine().trim();

        boolean isExist = summonerRepository.existsByUserIdAndSummonerNameAndTagLineIgnoreCaseAndNoSpaces(userId, summonerName, tagLine);
        if (isExist) throw new DataConflictException("이미 등록된 소환사입니다.");

        RiotAccountDTO account = riotApiClient.fetchAccountByRiotId(summonerName, tagLine).block(); //! aaaaaaa#1234 검색시 fetchAccountByRiotId api는 요청되는데,
        RiotSummonerDTO summoner = riotApiClient.fetchSummonerByPuuid(account.getPuuid()).block(); //! fetchSummonerByPuuid에서 404 나는 경우가 있더라.(riot계정은 있는데, 롤 소환사계정은 없는 경우인듯)
        RiotLeagueEntryDTO soloRank = riotApiClient.fetchSoloRankLeagueEntryByPuuid(account.getPuuid()).block();

        SummonerEntity summonerEntity = createSummonerEntity(null, userId, account, summoner, soloRank);
        SummonerEntity save = summonerRepository.save(summonerEntity);
        return SummonerDTO.fromEntity(save);
    }

    public SummonerDTO updateSummoner(UpdateSummonerReqeustDTO updateSummonerReqeustDTO){
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
        SummonerDTO summonerDTO = SummonerDTO.fromEntity(summonerEntity);
        return summonerDTO;
    }

    @Transactional
    public void deleteSummoner(Long no){
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        long deletedCount = summonerRepository.deleteByNoAndUserId(no, userId);
        if (deletedCount == 0) {
            throw new NotFoundDataException("소환사를 찾을 수 없거나 삭제할 권한이 없습니다.");
        }
    }

    private SummonerEntity createSummonerEntity(Long summonerNo, String userId, RiotAccountDTO account, RiotSummonerDTO summoner, RiotLeagueEntryDTO soloRank) {
        Tier tier = Optional.ofNullable(soloRank).map(RiotLeagueEntryDTO::getTier).orElse(Tier.UNRANKED);
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

