package com.ll.server.domain.member.service;

import com.ll.server.domain.follow.repository.FollowRepository;
import com.ll.server.domain.like.repository.LikeRepository;
import com.ll.server.domain.member.auth.dto.SignupRequestDto;
import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.dto.MemberUpdateParam;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.global.aws.s3.S3Service;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.response.exception.CustomRequestException;
import com.ll.server.global.security.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "MemberService")
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final RepostRepository repostRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public Member signup(SignupRequestDto requestDto) {
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

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new CustomException(ReturnCode.INVALID_REQUEST);
        }

        return member;
    }

    public Member findByEmail(String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다."));
    }

    // 마이페이지 - 사용자 정보 조회
    public MemberDto getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다."));

        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .build();
    }

    // 마이페이지 - 비밀번호 수정 (소셜 로그인 사용자는 비밀번호 변경 불가)
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        long id = AuthUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));

        if (member.getProvider() != Provider.LOCAL) {
            throw new CustomException(ReturnCode.INVALID_REQUEST);
        }

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
        }

        member.setPassword(passwordEncoder.encode(newPassword));
    }

    public Member getMemberById(Long writerId) {
        return memberRepository.findById(writerId)
                .orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public List<Member> getMembersByNickName(List<String> mentionedNames) {
        return memberRepository.findAllByNicknameIn(mentionedNames);
    }

    public Member findLocalMember(String email) {
        return memberRepository.findMemberByEmailAndProvider(email, Provider.LOCAL)
                .orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));
    }

    @Transactional
    public void updateMember(MemberUpdateParam param, MultipartFile imageFile) {
        Member member = memberRepository.findById(param.getMemberId()).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));

        if (param.getProfileUrl() != null) {
            s3Service.deleteFile(param.getProfileUrl());
        }

        String imageUrl = null;
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = s3Service.uploadFile(imageFile, "profile-images");
                param.changeProfileUrl(imageUrl);
            }
        } catch (IOException e) {
            throw new CustomException(ReturnCode.INTERNAL_ERROR);
        }

        member.update(param);
    }


}
