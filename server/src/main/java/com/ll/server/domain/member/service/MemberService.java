package com.ll.server.domain.member.service;

import com.ll.server.domain.member.MemberRole;
import com.ll.server.domain.member.dto.MemberRequest;
import com.ll.server.domain.member.dto.MemberResponse;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.exception.CustomException;
import com.ll.server.global.exception.ErrorCode;
import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;
import com.ll.server.global.rsData.RsData;
import com.ll.server.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public Member join(MemberRequest request) {
        return this.join(request.getEmail(), request.getPassword(), request.getRole(), request.getNickname(), request.getProviderId());
    }

    public MemberResponse getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .role(member.getRole().name())
                .build();

    }

    @Transactional
    public Member join(String email,
                       String password,
                       MemberRole role,
                       //String name,
                       String nickname,
                       String providerId) {

        // 존재하는 지 체크
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new CustomRequestException(ReturnCode.ALREADY_EXIST);
                });

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(com.ll.server.domain.member.enums.MemberRole.USER)
                .nickname(nickname)
                //.name(name)
                .provider(Provider.NAVER)
                .providerId(providerId)
                .build();

        String refreshToken = jwtProvider.genRefreshToken(member);
        member.setRefreshToken(refreshToken);

        return memberRepository.save(member);

    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public List<Member> getMembersByNickName(List<String> nickName) {
        return memberRepository.findMembersByNicknameIn(nickName);
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
        String nickname = (String) payloadBody.get("username");
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new SecurityUser(id, nickname, "", authorities);
    }

}
