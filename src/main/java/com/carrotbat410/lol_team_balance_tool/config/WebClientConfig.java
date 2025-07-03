package com.carrotbat410.lol_team_balance_tool.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${riot.api.base-url.kr}")
    private String riotApiBaseUrlKr;

    @Value("${riot.api.base-url.asia}")
    private String riotApiBaseUrlAsia;

    @Primary
    @Bean("krWebClient")
    public WebClient krWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(riotApiBaseUrlKr)
                .build();
    }

    @Bean("asiaWebClient")
    public WebClient asiaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(riotApiBaseUrlAsia)
                .build();
    }
}
