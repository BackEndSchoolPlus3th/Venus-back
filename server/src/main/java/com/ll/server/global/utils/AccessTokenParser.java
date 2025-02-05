package com.ll.server.global.utils;

import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class AccessTokenParser {
    public static Long getMemberIdByCookie(JwtProvider jwtProvider, HttpServletRequest request){
        Map<String, Object> claims = getClaims(jwtProvider, request);
        Integer memberId=(Integer) claims.get("id");
        return memberId.longValue();
    }

    public static String getNicknameByCookie(JwtProvider jwtProvider, HttpServletRequest request){
        Map<String, Object> claims = getClaims(jwtProvider, request);
        return (String)claims.get("username");
    }

    public static String getEmailByCookie(JwtProvider jwtProvider, HttpServletRequest request){
        Map<String, Object> claims = getClaims(jwtProvider, request);
        return (String)claims.get("email");
    }

    private static Map<String, Object> getClaims(JwtProvider jwtProvider, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null || cookies.length==0) throw new CustomException(ReturnCode.NOT_AUTHORIZED);

        String accessToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
        }
        if(accessToken.isBlank()) throw new CustomException(ReturnCode.NOT_AUTHORIZED);
        ;

        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        return claims;
    }
}
