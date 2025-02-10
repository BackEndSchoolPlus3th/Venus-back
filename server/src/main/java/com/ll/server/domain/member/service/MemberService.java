package com.ll.server.domain.member.service;

import com.ll.server.domain.member.auth.dto.SignupRequestDto;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "MemberService")
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Member signup (SignupRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ReturnCode.ALREADY_EXIST);
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(MemberRole.USER)
                .provider(Provider.LOCAL)
                .build();

        memberRepository.save(member);

        return member;
    }

    public Member findByEmail (String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public Member getMemberById(Long writerId) {
        return memberRepository.findById(writerId)
                .orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public List<Member> getMembersByNickName(List<String> mentionedNames) {
        return memberRepository.findAllByNicknameIn(mentionedNames);
    }
}
