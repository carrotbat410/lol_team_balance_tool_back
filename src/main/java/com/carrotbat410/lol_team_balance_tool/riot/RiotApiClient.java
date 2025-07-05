package com.carrotbat410.lol_team_balance_tool.riot;

import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotAccountDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotLeagueEntryDTO;
import com.carrotbat410.lol_team_balance_tool.dto.riot.RiotSummonerDTO;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.RiotApiNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RiotApiClient {

    private final WebClient krWebClient;
    private final WebClient asiaWebClient;

    @Value("${riot.api.key}")
    private String riotApiKey;

    public RiotApiClient(@Qualifier("krWebClient") WebClient krWebClient, @Qualifier("asiaWebClient") WebClient asiaWebClient) {
        this.krWebClient = krWebClient;
        this.asiaWebClient = asiaWebClient;
    }

    public Mono<RiotAccountDTO> fetchAccountByRiotId(String gameName, String tagLine) {
        return asiaWebClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}?api_key={apiKey}", gameName, tagLine, riotApiKey)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new RiotApiNotFoundException("Riot API request failed with status code: " + clientResponse.statusCode())))
                .bodyToMono(RiotAccountDTO.class);
    }

    public Mono<RiotSummonerDTO> fetchSummonerByPuuid(String puuid) {
        System.out.println("puuid:"+puuid);
        return krWebClient.get()
                .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}?api_key={apiKey}", puuid, riotApiKey)
                .retrieve()
                .bodyToMono(RiotSummonerDTO.class);
    }

    public Mono<RiotLeagueEntryDTO[]> fetchLeagueEntryByPuuid(String puuid) {
        return krWebClient.get()
                .uri("/lol/league/v4/entries/by-puuid/{puuid}?api_key={apiKey}", puuid, riotApiKey)
                .retrieve()
                .bodyToMono(RiotLeagueEntryDTO[].class);
    }
}
