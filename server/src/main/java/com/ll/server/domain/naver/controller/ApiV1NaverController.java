package com.ll.server.domain.naver.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.domain.naver.service.NaverService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class ApiV1NaverController {

    private final NaverService naverService;
    private final MemberService memberService;

    @Value("${spring.oauth2.naver.client-id}")
    private String clientId;

    @Value("${spring.oauth2.naver.url.redirect-uri}")
    private String redirectURI;

    // 네이버 사용자 인증 후
    @GetMapping("/api/auth/naver")
    public String loginCallback(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws IOException, InterruptedException {
        // code와 state 값 확인
        System.out.println("Code: " + code);
        System.out.println("State: " + state);

        // 액세스 토큰 요청 url
        String token_url = "https://nid.naver.com/oauth2.0/token?"
                + "grant_type=authorization_code"
                + "&client_id=hjXAKRZSt03GUwcpKpVm"
                + "&client_secret=cOZFIDt4t9"
                + "&code=" + code
                + "&state=" + state;

        // 액세스 토큰 요청
        String token = naverService.askToken(token_url);
        System.out.println("token: " + token);

        // json 객체 변환
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(token);

        // 액세스 토큰 추출
        String accessToken = jsonNode.get("access_token").asText();
        System.out.println("Access Token: " + accessToken);

        // 사용자 정보 요청
        String userInfo = naverService.askUserInfo(accessToken);
        System.out.println("User Info: " + userInfo);

        // json 객체 변환
        ObjectMapper objectMapper_u = new ObjectMapper();
        JsonNode jsonNode_u = objectMapper_u.readTree(userInfo);
        JsonNode jsonNode_r = jsonNode_u.get("response");

        // 사용자 정보 추출
        String id = jsonNode_r.get("id").asText();
        String nickname = jsonNode_r.get("nickname").asText();
        String email = jsonNode_r.get("email").asText();
        String name = jsonNode_r.get("name").asText();

        System.out.println("id: " + id);
        System.out.println("nickname: " + nickname);
        System.out.println("email: " + email);
        System.out.println("name: " + name);

        // 사용자 정보 있는지 확인(email 활용)
        boolean exists = naverService.isMemberExists(email);

        if (exists) {
            System.out.println("회원 존재");
            // jwt 토큰 생성하고 DB저장 및 메인화면으로 리다이렉트
            naverService.tokenGengerate(userInfo);
            return "redirect:/home";

        } else {
            System.out.println("회원 비존재");
            // 회원가입화면으로 리다이렉트
            return "redirect:/home";
        }
    }




}