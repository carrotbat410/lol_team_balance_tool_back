package com.carrotbat410.lol_team_balance_tool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${riot.api.baseurl.kr}")
    private String riotApiBaseurlKr;

    @Value("${riot.api.baseurl.asia}")
    private String riotApiBaseurlAsia;

    @Primary
    @Bean("krWebClient")
    public WebClient krWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(riotApiBaseurlKr)
                .build();
    }

    @Bean("asiaWebClient")
    public WebClient asiaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(riotApiBaseurlAsia)
                .build();
    }
}