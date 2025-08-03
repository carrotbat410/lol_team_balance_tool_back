package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AddSummonerRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotAccountDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotLeagueEntryDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotSummonerDTO;
import com.carrotbat410.lol_team_balance_tool.entity.SummonerEntity;
import com.carrotbat410.lol_team_balance_tool.entity.Tier;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.riot.RiotApiClient;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.carrotbat410.lol_team_balance_tool.entity.Tier.GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummonerServiceTest {

    @InjectMocks
    private SummonerService summonerService;

    @Mock
    private SummonerRepository summonerRepository;

    @Mock
    private RiotApiClient riotApiClient;

    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        // SecurityUtils.getCurrentUserIdFromAuthentication() 정적 메소드 Mocking
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        // 매 테스트 후 Mock 객체 해제
        securityUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("사용자의 모든 소환사 정보를 성공적으로 조회한다")
    void findSummoners_Success() {
        // given
        String testUserId = "testUser";
        SummonerEntity summonerEntity = new SummonerEntity(1L, testUserId, "통티모바베큐", "0410", GOLD, 1, 13, 150, 55, 45, 123);

        given(SecurityUtils.getCurrentUserIdFromAuthentication()).willReturn(testUserId);
        given(summonerRepository.findByUserId(testUserId, PageRequest.of(0, 5))).willReturn(new PageImpl<>(Collections.singletonList(summonerEntity)));

        // when
        Page<SummonerDTO> result = summonerService.findSummoners(PageRequest.of(0, 5));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSummonerName()).isEqualTo("통티모바베큐");
        assertThat(result.getContent().get(0).getTier()).isEqualTo(GOLD);
    }

    @Test
    @DisplayName("새로운 소환사를 성공적으로 저장한다")
    void saveSummoner_Success() {
        // given
        String testUserId = "testUser";
        String summonerName = "통티모바베큐";
        String tagLine = "0410";
        AddSummonerRequestDTO requestDTO = new AddSummonerRequestDTO(summonerName, tagLine);

        // Mocking SecurityUtils
        given(SecurityUtils.getCurrentUserIdFromAuthentication()).willReturn(testUserId);

        // Mocking Repository - 소환사가 이미 존재하지 않음
        given(summonerRepository.existsByUserIdAndSummonerNameAndTagLineIgnoreCaseAndNoSpaces(testUserId, summonerName, tagLine)).willReturn(false);

        // Mocking Riot API Client - API 호출 결과 미리 정의
        RiotAccountDTO accountDTO = new RiotAccountDTO("test_puuid", summonerName, tagLine);
        RiotSummonerDTO summonerDTO = new RiotSummonerDTO("test_id", "test_puuid", "RiotSummonerName", 150, 500, 1L);
        RiotLeagueEntryDTO leagueEntryDTO = new RiotLeagueEntryDTO("leagueId","통티모바베큐" ,"RANKED_SOLO_5x5", GOLD, "I",  10, 80, 70);

        given(riotApiClient.fetchAccountByRiotId(summonerName, tagLine)).willReturn(Mono.just(accountDTO));
        given(riotApiClient.fetchSummonerByPuuid(accountDTO.getPuuid())).willReturn(Mono.just(summonerDTO));
        given(riotApiClient.fetchSoloRankLeagueEntryByPuuid(accountDTO.getPuuid())).willReturn(Mono.just(leagueEntryDTO));

        // when
        summonerService.saveSummoner(requestDTO);

        // then
        // summonerRepository.save() 메소드가 호출되었는지 검증
        // ArgumentCaptor를 사용하여 save 메소드에 전달된 SummonerEntity 객체를 캡처
        ArgumentCaptor<SummonerEntity> captor = ArgumentCaptor.forClass(SummonerEntity.class);
        verify(summonerRepository).save(captor.capture());
        SummonerEntity savedEntity = captor.getValue();

        // 캡처된 SummonerEntity의 필드 값들이 예상과 일치하는지 검증
        assertEquals(testUserId, savedEntity.getUserId());
        assertEquals(summonerName, savedEntity.getSummonerName());
        assertEquals(tagLine, savedEntity.getTagLine());
        assertEquals(GOLD, savedEntity.getTier());
        assertEquals(1, savedEntity.getRank1());
        assertEquals(150, savedEntity.getLevel());
        assertEquals(80, savedEntity.getWins());
        assertEquals(70, savedEntity.getLosses());
        assertEquals(500, savedEntity.getIconId());
    }

}
