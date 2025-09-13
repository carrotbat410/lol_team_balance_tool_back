package com.carrotbat410.lol_team_balance_tool.config;

import com.carrotbat410.lol_team_balance_tool.jwt.JWTFilter;
import com.carrotbat410.lol_team_balance_tool.jwt.JWTUtil;
import com.carrotbat410.lol_team_balance_tool.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    //* INFO SecurityFilter(LoginFilter 포함)에 대한 CORS 설정
                    //* LoginFilter는 이걸로 충분함. RestAPI요청에 대한 CORS 설정은 Spring MVC레벨에서 설정해줘야함 -> CorsMvcConfig.java
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://172.30.1.52:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization")); //* 백엔드 -> 프론트로 가는 response header에 Authorization를 set할거기떄문에 허용해줘야함.

                        return configuration;
                    }
                })));

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api", "/api/join","/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api/team/balance", "/api/tmpHealthCheck").permitAll()
                        .requestMatchers("/api/admin").hasRole("ADMIN")
                                .anyRequest().authenticated());//그 외 요청은 로그인한 사용자만 가능하도록

        http
                .addFilterAfter(new JWTFilter(jwtUtil), LoginFilter.class); //* addFilterBefore이 아닌, addFilterAfter를 이용해서 LoginFilter 앞이 아닌 뒤에 두는게 맞다 생각함.
                //LoginFilter - 로그인요청시 jwt 생성.
                //JWTFilter - api호출시 권한 검증.


        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));//스프링 시큐리티가 세션을 만들지 않고, 사용하지도 않겠다는 의미.

        return http.build();
    }
}