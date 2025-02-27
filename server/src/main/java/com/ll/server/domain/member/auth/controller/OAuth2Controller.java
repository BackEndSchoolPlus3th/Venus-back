package com.ll.server.domain.member.auth.controller;

import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.security.custom.CustomOAuth2User;
import com.ll.server.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j(topic = "OAuth2Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/callback/kakao")
    public void kakaoLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
        processOAuth2Login(authentication, response);
    }

    @GetMapping("/callback/naver")
    public void naverLoginSuccess(Authentication authentication, HttpServletResponse response) throws IOException {
        processOAuth2Login(authentication, response);
    }

    private void processOAuth2Login(Authentication authentication, HttpServletResponse response) throws IOException {

        if (authentication == null) {
            log.error("Authentication 객체가 null 입니다.");
            response.sendRedirect("/login?error=authentication_failed"); // 오류 페이지로 리다이렉트
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Member member = oAuth2User.getMember();
        MemberDto memberDto = new MemberDto(member);

        String accessToken = jwtUtil.generateAccessToken(memberDto);
        String refreshToken = jwtUtil.generateRefreshToken(memberDto.getEmail());

        jwtUtil.addJwtToCookie(accessToken, response, "accessToken");
        jwtUtil.addJwtToCookie(refreshToken, response, "refreshToken");


    }
}
