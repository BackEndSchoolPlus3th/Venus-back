package com.ll.server.domain.member.service;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.rsData.RsData;
import com.ll.server.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Member join(String email, String password, String name, String nickname, String providerId) {

        // 존재하는 지 체크
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new IllegalArgumentException("member already exists with " + member);
                });

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .name(name)
                .provider("naver")
                .providerId(providerId)
                .build();

        String refreshToken = jwtProvider.genRefreshToken(member);
        member.setRefreshToken(refreshToken);

        return memberRepository.save(member);

    }

    public Member getMember(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) {
            throw new NoSuchElementException("No member found with email: " + email);
        }
        return member.orElse(null);
    }


    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        return jwtProvider.verify(token);
    }

    // 토큰갱신
    public RsData<String> refreshAccessToken(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
        String accessToken = jwtProvider.genAccessToken(member);
        return new RsData<>("200", "토큰 갱신에 성공하였습니다.", accessToken);
    }
    // 토큰으로 User 정보 가져오기
    public SecurityUser getUserFromAccessToken(String accessToken) {
        Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);
        long id = (int) payloadBody.get("id");
        String username = (String) payloadBody.get("username");
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new SecurityUser(id, username, "", authorities);
    }

}
