package com.carrotbat410.lol_team_balance_tool.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//* Spring MVC 레벨에서 CORS 설정
//* 즉,@RestController로 만든 API에 대해서만 동작함.
//* Spring Security가 적용되지 않은 컨트롤러를 위해 추가해줌.
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://lolcivilwarhelper.site", "http://www.lolcivilwarhelper.site", "https://lolcivilwarhelper.site", "https://www.lolcivilwarhelper.site");
    }
}
