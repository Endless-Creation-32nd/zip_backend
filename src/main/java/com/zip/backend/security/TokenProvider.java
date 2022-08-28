package com.zip.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

// 스프링 컨테이너에 등록되어 사용되는 클래스
@Component
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    // appliacation.properties 에서 값을 가져오는 방법 : @Value
    @Value("${app.auth.tokenSecret}")
    private String secretKey;

    @Value("${app.auth.tokenExpirationMsec}")
    private long tokenValidMilliSecond; // 토큰 유효기간 1일

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    // JWT 을 만들어내는 메소드
    public String createToken(Authentication authentication) {
        // token 을 만들기 위해 Authentication 객체를 불러오게 된다
        UserPrincipal userPrincipal= (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidMilliSecond);

        // Token : header / payload / signature
        // 이 중에서 payload 에 해당하는 부분을 작성하는 부분과 signature 부분 생성
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getUser().getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact();
    }

    // 필터에서 token 유효성 검증 후 SecurityContextHolder 에 저장할 Authentication 을 생성
    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // JWT 을 입력받아 UserId를 가져오는 메소드
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    // JWT 를 입력받아 유효한 토큰인지 확인하는 메소드
    // 특정 API 를 활용할 때 client 가 유효한 JWT 을 보내는지 확인할 수 있음
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("유효하지 않은 JWT 서명");
        } catch (MalformedJwtException ex) {
            logger.error("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException ex) {
            logger.error("만료된 JWT 토큰");
        } catch (UnsupportedJwtException ex) {
            logger.error("지원하지 않는 JWT 토큰");
        } catch (IllegalArgumentException ex) {
            logger.error("비어있는 토큰");
        }
        return false;
    }
}
