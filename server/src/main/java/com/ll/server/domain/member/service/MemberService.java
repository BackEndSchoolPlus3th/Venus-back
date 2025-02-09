package com.ll.server.domain.member.service;

import com.ll.server.domain.member.MemberRole;
import com.ll.server.domain.member.dto.MemberRequest;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(MemberRequest request) {
        return this.join(request.getEmail(), request.getPassword(), request.getRole(), request.getNickname(), request.getProviderId());
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
                .role(role)
                .nickname(nickname)
                //.name(name)
                .provider("naver")
                .providerId(providerId)
                .build();

//        String refreshToken = jwtProvider.genRefreshToken(member);
//        member.setRefreshToken(refreshToken);

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
}
