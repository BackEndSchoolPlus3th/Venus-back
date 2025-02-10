package com.ll.server.domain.kakao.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.server.domain.kakao.service.KakaoService;
import com.ll.server.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class ApiV1KakaoController {
    private final KakaoService kakaoService;
    private final MemberService memberService;

    @Value("${spring.oauth2.kakao.client-id}")
    private String clientId;

    @Value("${spring.oauth2.kakao.url.redirect-uri}")
    private String redirectURI;


    @Value("${spring.oauth2.kakao.client-secret}")
    private String clientSecret;

    @GetMapping("/api/auth/kakao")
    public String loginCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException, InterruptedException {
        // code와 state 값 확인
        System.out.println("Code: " + code);
        if (code == null) {
            return "Code not found!";
        }
        System.out.println("Received code: " + code);
        //액세스토큰 요청
        String token_url = "https://kauth.kakao.com/oauth/token?"
                + "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectURI
                + "&code=" + code
                + "&client_secret=" + clientSecret;

        // 액세스 토큰 요청
        String token = kakaoService.askToken(token_url);
        System.out.println("token: " + token);

        // json 객체 변환
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(token);

        // 액세스 토큰 추출
        String accessToken = jsonNode.get("access_token").asText();
        System.out.println("Access Token: " + accessToken);

        // 사용자 정보 요청
        String userInfo = kakaoService.askUserInfo(accessToken);
        System.out.println("User Info: " + userInfo);
        return null;
    }
}
