package com.ll.server.domain.member.auth.service;

import com.ll.server.domain.member.auth.dto.AuthResponse;
import com.ll.server.domain.member.auth.dto.LoginRequest;
import com.ll.server.domain.member.auth.dto.SignupRequest;
import com.ll.server.domain.member.auth.dto.SocialLoginRequest;
import com.ll.server.domain.member.auth.entity.RefreshToken;

import com.ll.server.domain.member.auth.repository.RefreshTokenRepository;
import com.ll.server.domain.member.auth.security.jwt.JwtTokenProvider;
import com.ll.server.domain.member.auth.security.oauth2.CustomOAuth2UserService;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.exception.CustomException;
import com.ll.server.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Transactional
    public void signup(SignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        Member member = Member.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .role(MemberRole.USER)
                .provider(Provider.LOCAL)
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. 실제로 인증이 이루어지는 부분
        // authenticate() Method가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername Method가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 4. RefreshToken Redis 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(loginRequest.getEmail())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 5. AccessToken, RefreshToken 쿠키에 저장
        jwtTokenProvider.setTokenInCookie(accessToken, refreshToken, response);

        // 6. 토큰 정보가 담긴 DTO 객체 반환
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        // 1. Refresh Token 검증
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        if (!jwtTokenProvider.validateToken(refreshToken, authentication)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2.RefreshToken DB에서 RefreshToken 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 3. Refresh Token Redis에서 삭제
        refreshTokenRepository.delete(refreshTokenEntity);

        // 4. 새로운 AccessToken과 RefreshToken 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 5. 새로운 RefreshToken Redis에 저장
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .email(authentication.getName())
                .build();

        refreshTokenRepository.save(newRefreshTokenEntity);

        // 6.AccessToken, RefreshToken 쿠키에 저장
        jwtTokenProvider.setTokenInCookie(newAccessToken, newRefreshToken, response);

        // 7. 토큰 정보가 담긴 DTO 객체 반환
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public AuthResponse socialLogin(SocialLoginRequest socialLoginRequest, HttpServletResponse response) {
        // Authorization Code 를 사용하여 AccessToken 요청
        Authentication authentication = customOAuth2UserService.loadUser(socialLoginRequest.getProvider(), socialLoginRequest.getAuthorizationCode());

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 4. RefreshToken Redis에 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(authentication.getName())
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        // 5. AccessToken, RefreshToken 쿠키에 저장
        jwtTokenProvider.setTokenInCookie(accessToken, refreshToken, response);

        // 6. 토큰 정보가 담긴 DTO 객체 반환
        return new AuthResponse(accessToken, refreshToken);
    }
}
