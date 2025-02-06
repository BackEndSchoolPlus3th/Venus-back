package com.ll.server.global.security;

import com.ll.server.domain.member.service.MemberService;
import com.ll.server.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;
    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        if (request.getRequestURI().equals("/api/v1/members/login") ||
                request.getRequestURI().equals("/api/v1/members/logout") ||
                request.getRequestURI().equals("/api/v1/members/signup") ||
                request.getRequestURI().equals("/api/v1/members/test") ||
                request.getRequestURI().equals("/api/v1/news/**")
        ) {  // 로그인, 로그아웃, 회원가입, 뉴스 목록은 인증 없이 가능
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = _getCookie("accessToken");
        // accessToken 검증 or refreshToken 발급
        if (accessToken!=null && !accessToken.isBlank()) {
            // 토큰 유효기간 검증
            if (!memberService.validateToken(accessToken)) {
                String refreshToken = _getCookie("refreshToken");
                RsData<String> rs = memberService.refreshAccessToken(refreshToken);
                _addHeaderCookie("accessToken", rs.getData());
            }
            // securityUser 가져오기
            SecurityUser securityUser = memberService.getUserFromAccessToken(accessToken);
            System.out.println("securityUser = " + securityUser);
            // 인가 처리
            SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
        }
        filterChain.doFilter(request, response);
    }
    private String _getCookie(String name) {
        Cookie[] cookies = req.getCookies();
        if(cookies==null || cookies.length==0) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }
    private void _addHeaderCookie(String tokenName, String token) {
        ResponseCookie cookie = ResponseCookie.from(tokenName, token)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());
    }
}
