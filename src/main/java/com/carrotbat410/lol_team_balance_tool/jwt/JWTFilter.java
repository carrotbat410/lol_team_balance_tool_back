package com.carrotbat410.lol_team_balance_tool.jwt;

import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더 탐색
        String authorization= request.getHeader("Authorization");

        // 2. Authorization 헤더 검증 (null 또는 "Bearer " 시작이 아닌 경우)
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // 다음 필터로 넘기고 메서드 종료
        }

        // 3. "Bearer " 부분 제거 후 순수 토큰 획득
        String token;
        try {
            token = authorization.split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8"); //* INFO 인코딩 설정 안해주면 한글이 ?로 찍힘.  기본적으로 서블릿 응답은 인코딩을 지정하지 않으면 ISO-8859-1로 처리됨. UTF-8 한글은 ISO-8859-1에서 표현 불가
            response.getWriter().write("{\"error\":\"Invalid token format.\"}");
            return;
        }

        // 4. 토큰 검증 (파싱 과정에서 모든 JWT 예외 처리)
        try {
            // 토큰 만료 여부 확인 (만료 시 ExpiredJwtException 발생)
            if (jwtUtil.isExpired(token)) {
                throw new ExpiredJwtException(null, null, "Token has expired");
            }

            // 토큰에서 username과 role 획득
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            // UserEntity 생성 및 값 설정
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(username);
            userEntity.setPassword("temppassword"); // 실제 DB 조회가 아니므로 임시 비밀번호 설정
            userEntity.setRole(role);

            // UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            // 세션에 사용자 등록 (인증 완료)
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (JwtException e) {
            // JWT 관련 모든 예외 (만료, 형식 오류 등) 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Invalid or expired token.\", \"message\":\"" + e.getMessage() + "\"}");
            return; // 예외 발생 시 필터 체인 중단
        }

        // 5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}