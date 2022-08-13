package com.zip.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;


    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // header 에서 JWT 가져오기
            String jwt = getJwtFromRequest(request);

            // JWT 의 유효성 검증
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication= tokenProvider.getAuthentication(jwt);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Security Context 에서 사용자 인증을 설정할 수 없습니다", ex);
        }

        filterChain.doFilter(request,response); // 서블릿을 실행하는 메소드
    }

    // header 에 "Authorization" key 에 해당하는 value 값이 다음과 같다
    // Bearer access_token
    // 검증은 Bearer access_token 이 null 이 아니고 길이가 0보다 큰지 + Bearer 로 시작하는지
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
