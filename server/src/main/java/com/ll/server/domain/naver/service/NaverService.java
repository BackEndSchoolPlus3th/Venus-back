package com.ll.server.domain.naver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NaverService {

    private final MemberRepository memberRepository;

    // 토큰 요청
    public static String askToken(String token_url) throws IndexOutOfBoundsException, InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(token_url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // 사용자정보 요청
    public String askUserInfo(String accessToken) throws IOException, InterruptedException {

        // 사용자 정보 요청 URL
        String URL = "https://openapi.naver.com/v1/nid/me";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response =client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // 회원 유무 확인
    public boolean isMemberExists(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);

        return member.isPresent();
    }

    @Transactional
    // jwt 토큰 만들기
    public void tokenGengerate(String userInfo) throws JsonProcessingException {

        final String SECRET_KEY = "${spring.jwt.secret-key}";
        final SecretKey SECRET_KEY_2 = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//        String encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
//        System.out.println("Base64 Encoded Secret Key: " + SECRET_KEY);

        ObjectMapper objectMapper_t = new ObjectMapper();
        JsonNode objectMapper_2 = objectMapper_t.readTree(userInfo);
        JsonNode jsonNode_t = objectMapper_2.get("response");

        String id = jsonNode_t.get("id").asText();
        String nickname = jsonNode_t.get("nickname").asText();
        String email = jsonNode_t.get("email").asText();
        String name = jsonNode_t.get("name").asText();

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600000; //  1시간 후 만료

        String jwtToken =  Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("username", name)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_2)
                .compact();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        member.setRefreshToken(jwtToken);
        //memberRepository.save(member);

        return ;



    }
}
