package com.ll.server.global.security.custom;

import com.ll.server.domain.member.auth.interfaces.OAuth2UserInfo;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "CustomOAuth2UserService")
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser (OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User Attributes: " + oAuth2User.getAttributes());
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User (OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes()); // Factory Pattern 적용

        Optional<Member> memberOptional = memberRepository.findMemberByEmail(oAuth2UserInfo.getEmail());

        Member member = memberOptional.map(existingMember -> {
                    //기존 member 가 있다면, update 로직을 실행합니다.
                    return updateMember(existingMember, oAuth2UserInfo);
                })
                .orElseGet(() -> createMember(oAuth2UserInfo, provider));

        return new CustomOAuth2User(oAuth2User, member);
    }

    @Transactional
    protected Member createMember (OAuth2UserInfo oAuth2UserInfo, String provider) {
        // 소셜 로그인 사용자는 어차피 비밀번호로 로그인하지 않으므로, UUID를 비밀번호로
        String randomPassword = UUID.randomUUID().toString();
        String realPassword = passwordEncoder.encode(randomPassword);

        Member member = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .password(realPassword)
                .provider(Provider.valueOf(provider.toUpperCase()))
                .providerId(oAuth2UserInfo.getId())
                .role(MemberRole.USER)
                .build();
        try {
            return memberRepository.save(member);
        } catch (Exception e) {
            String errorMessage = String.format("회원 저장에 실패했습니다: %s", e.getMessage());
            log.error(errorMessage);
            throw new CustomException(ReturnCode.INTERNAL_ERROR);
        }
    }

    @Transactional
    protected Member updateMember(Member existingMember, OAuth2UserInfo oAuth2UserInfo) {
        // OAuth2 정보와 일치하도록 기존 회원 정보 업데이트
        existingMember.setNickname(oAuth2UserInfo.getName());
        existingMember.setModifyDate(LocalDateTime.now());
        // 필요한 다른 정보들도 업데이트

        try {
            return memberRepository.save(existingMember);
        } catch (Exception e) {
            String errorMessage = String.format("OAuth2 Provider(%s) : 회원 정보 업데이트에 실패했습니다: %s", existingMember.getProvider(), e.getMessage());
            log.error(errorMessage);
            throw new CustomException(ReturnCode.INTERNAL_ERROR);
        }
    }
}
