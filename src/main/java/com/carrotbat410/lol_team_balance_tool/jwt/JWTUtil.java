package com.carrotbat410.lol_team_balance_tool.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        }catch (ExpiredJwtException e) {
            return true;
        }catch (Exception e) {
            System.out.println("JWT isExpired 메서드 실행 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) //현재 발행시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}