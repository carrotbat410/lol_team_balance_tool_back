package com.carrotbat410.lol_team_balance_tool.jwt;

import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
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

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) { // 토큰 없으면 이쪽을 통해 다음 필터로 넘어감
            System.out.println("token없는 요청임. 이쪽을 통해 다음 필터로 넘어감");
            filterChain.doFilter(request, response);

            return; //조건이 해당되면 메소드 종료 (필수)
        }

        System.out.println("token 포함된 요청임");

        //Bearer 부분 제거 후 순수 토큰만 획득
        String[] authorizationValue = authorization.split(" ");
        if(authorizationValue.length != 2){
            System.out.println("Authorization 헤더값이 이상함.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8"); //* INFO 인코딩 설정 안해주면 한글이 ?로 찍힘.  기본적으로 서블릿 응답은 인코딩을 지정하지 않으면 ISO-8859-1로 처리됨. UTF-8 한글은 ISO-8859-1에서 표현 불가
            response.getWriter().write("{\"error\":\"잘못된 형식의 토큰값이 감지되었습니다.\"}");
        }
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("토큰 만료됨.");
            //조건이 해당되면 메소드 종료 (필수)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8"); //* INFO 인코딩 설정 안해주면 한글이 ?로 찍힘.  기본적으로 서블릿 응답은 인코딩을 지정하지 않으면 ISO-8859-1로 처리됨. UTF-8 한글은 ISO-8859-1에서 표현 불가
            response.getWriter().write("{\"error\":\"토큰이 만료되었습니다.\"}");
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}