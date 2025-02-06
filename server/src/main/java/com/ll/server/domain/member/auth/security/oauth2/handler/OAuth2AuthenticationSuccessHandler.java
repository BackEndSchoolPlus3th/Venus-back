package com.ll.server.domain.member.auth.security.oauth2.handler;

import com.ll.server.domain.member.auth.entity.RefreshToken;
import com.ll.server.domain.member.auth.repository.RefreshTokenRepository;
import com.ll.server.domain.member.auth.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

//@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInSeconds;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // RefreshToken Redis 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(authentication.getName())
                .expiration(refreshTokenValidityInSeconds / 1000)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // AccessToken, RefreshToken 쿠키에 저장
        jwtTokenProvider.setTokenInCookie(accessToken, refreshToken, response);

        // Redirect (프론트 URL로, 토큰을 쿼리 파리미터나 헤더에 담아 전달 가능)
        // TODO: Redirect URL Endpoint 설정 확인하기
        // response.sendRedirect("http://localhost:8080/");
    }
}
