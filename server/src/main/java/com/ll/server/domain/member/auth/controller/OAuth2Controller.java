package com.ll.server.domain.member.auth.controller;

import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.security.custom.CustomOAuth2User;
import com.ll.server.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

//이는 프론트가 있으면 구현할 필요가 없음
@Slf4j(topic = "OAuth2Controller")
@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final JwtUtil jwtUtil;

    @Value("${front.redirect-url}")
    private String redirectUrl;

    @GetMapping("/non1")
    public void kakaoLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
        processOAuth2Login(authentication, response);
    }

    @GetMapping("/non2")
    public void naverLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
        processOAuth2Login(authentication, response);
    }

    private void processOAuth2Login (Authentication authentication, HttpServletResponse response) throws IOException {
        log.info("오쓰로그인");
        if (authentication == null) {
            log.error("Authentication 객체가 null 입니다.");
            response.sendRedirect("/login?error=authentication_failed"); // 오류 페이지로 리다이렉트
            return;
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Member member = oAuth2User.getMember();

        String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

        jwtUtil.addJwtToCookie(accessToken, response, "accessToken");
        jwtUtil.addJwtToCookie(refreshToken, response, "refreshToken");

        // Redirect url (front)
        response.sendRedirect(redirectUrl + "?accessToken=" + accessToken + "&refreshToken=" + refreshToken);
    }
}
