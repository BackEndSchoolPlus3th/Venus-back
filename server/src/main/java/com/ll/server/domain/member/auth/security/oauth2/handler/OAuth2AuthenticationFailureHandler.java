package com.ll.server.domain.member.auth.security.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        // OAuth2 인증 실패 시 처리 (예: 에러 메세지 전달, 리다이렉트)
        /* TODO: Redirect Uri 추후 수정하기 */
        response.sendRedirect("/login");
    }
}
