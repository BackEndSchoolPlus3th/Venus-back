package com.ll.server.domain.member.auth.security.oauth2;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.global.exception.CustomException;
import com.ll.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    public Authentication loadUser(Provider provider, String authorizationCode) {
        Member member = getUserInfo(provider, authorizationCode);
        Member existingMember = memberRepository.findByProviderAndProviderId(provider.toString(), member.getProviderId())
                .orElseGet(() -> memberRepository.save(member));

        // CustomOAuth2User 객체 생성하여 반환 (OAuth2User 인터페이스 구현)
        return new UsernamePasswordAuthenticationToken(existingMember, null, existingMember.getAuthorities());
    }

    private Member getUserInfo(Provider provider, String authorizationCode) {
        if (provider == Provider.KAKAO) {
            return getKakaoUserInfo(authorizationCode);
        } else if (provider == Provider.NAVER) {
            return getNaverUserInfo(authorizationCode);
        } else {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Member getKakaoUserInfo(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    kakaoTokenUri,
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            return getKakaoUserInfoByToken(accessToken);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Member getKakaoUserInfoByToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> kakaoTokenRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    kakaoUserInfoUri,
                    HttpMethod.GET,
                    kakaoTokenRequest,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                String id = (String) responseBody.get("id");
                Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
                String email = (String) kakaoAccount.get("email");
                Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
                String nickname = (String) kakaoProfile.get("nickname");

                return Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .provider(Provider.KAKAO)
                        .providerId(id)
                        .role(MemberRole.USER)
                        .password("kakao_social_login")
                        .build();
            } else {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Member getNaverUserInfo(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("redirect_uri", naverRedirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    naverTokenUri,
                    HttpMethod.POST,
                    naverTokenRequest,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            return getNaverUserInfoByToken(accessToken);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Member getNaverUserInfoByToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    naverUserInfoUri,
                    HttpMethod.GET,
                    naverTokenRequest,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> naverResponse = (Map<String, Object>) responseBody.get("response");
                String id = (String) naverResponse.get("id");
                String email = (String) naverResponse.get("email");
                String nickname = (String) naverResponse.get("nickname");

                return Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .provider(Provider.NAVER)
                        .providerId(id)
                        .role(MemberRole.USER)
                        .password("naver_social_login")
                        .build();
            } else {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
