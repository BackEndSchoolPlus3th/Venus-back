package com.ll.server.global.security.custom;

import com.ll.server.domain.member.auth.interfaces.KakaoUserInfo;
import com.ll.server.domain.member.auth.interfaces.NaverUserInfo;
import com.ll.server.domain.member.auth.interfaces.OAuth2UserInfo;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {

        if (provider.equals("kakao")) {
            return new KakaoUserInfo(attributes);
        } else if (provider.equals("naver")) {
            return new NaverUserInfo(attributes);
        } else {
            throw new CustomException(ReturnCode.WRONG_PARAMETER);
        }
    }
}

